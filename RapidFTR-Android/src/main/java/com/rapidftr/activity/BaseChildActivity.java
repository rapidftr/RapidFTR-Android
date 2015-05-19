package com.rapidftr.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.rapidftr.R;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.Cleanup;
import org.json.JSONException;

public abstract class BaseChildActivity extends CollectionActivity {

    public static final int CLOSE_ACTIVITY = 999;

    protected Child child;
    protected boolean editable = true;

    @Override
    protected Boolean getEditable() {
        return editable;
    }

    @Override
    protected BaseModel getModel() {
        return child;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("child_state", child.toString());
    }

    @Override
    protected void onStop() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    protected abstract void initializeView();

    protected abstract void saveChild();

    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        this.formSections = getFormService().getFormSections(Child.CHILD_FORM_NAME);

        if (savedInstanceState != null && savedInstanceState.containsKey("child_state")) {
            this.child = new Child(savedInstanceState.getString("child_state"));
        } else if (child == null) {
            child = new Child();
        }
    }

    public Child load() throws JSONException {
        @Cleanup ChildRepository repository = inject(ChildRepository.class);
        String childId = getIntent().getExtras().getString("id");
        child = repository.get(childId);
        return child;
    }

    public Child save() throws JSONException {
        if (!child.isValid()) {
            makeToast(R.string.save_child_invalid);
            return null;
        }

        if (child.isNew()) {
            child.setCreatedBy(getCurrentUser().getUserName());
            child.setOrganisation(getCurrentUser().getOrganisation());
        }

        child.generateUniqueId();
        child.setSynced(false);
        @Cleanup ChildRepository repository = inject(ChildRepository.class);
        repository.createOrUpdate(child);
        return child;
    }



    public void view() throws JSONException {
        Intent intent = new Intent(this, ViewChildActivity.class);
        intent.putExtra("id", child.getUniqueId());
        child.put("saved", true);
        finish();
        startActivity(intent);
    }

    protected void edit() throws JSONException {
        Intent intent = new Intent(this, EditChildActivity.class);
        intent.putExtra("id", child.getUniqueId());
        finish();
        startActivity(intent);
    }

    protected SaveChildTask getSaveChildTask() {
        return new SaveChildTask();
    }


    protected class SaveChildTask extends AsyncTaskWithDialog<Void, Void, Child> {
        @Override
        protected Child doInBackground(Void... params) {
            try {
                return save();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Child result) {
            try {
                if (result != null)
                    view();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void cancel() {
            this.cancel(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            hideEnquiriesTabIfRapidReg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void showAlertDialog() {
        DialogInterface.OnClickListener listener = createAlertListener();
        saveOrDiscardOrCancelChild(listener);
    }

    private DialogInterface.OnClickListener createAlertListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItem) {
                switch (selectedItem) {
                    case 0:
                        saveChild();
                        break;
                    case 1:
                        superBackPressed();
                    case 2:
                        break;
                }
            }
        };
    }

    private void superBackPressed() {
        super.onBackPressed();
    }

}
