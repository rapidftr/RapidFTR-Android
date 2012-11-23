package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.*;
import com.rapidftr.R;
import com.rapidftr.adapter.FormSectionPagerAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.Cleanup;
import org.json.JSONException;

import java.util.List;

public abstract class BaseChildActivity extends RapidFtrActivity {

    public static final int CLOSE_ACTIVITY = 999;

    protected List<FormSection> formSections;
    protected Child child;
    protected boolean editable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            initializeView();
            initializeData(savedInstanceState);
            initializePager();
            initializeSpinner();
            initializeLabels();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("child_state", child.toString());
    }

    protected void setLabel(int label) {
        ((Button) findViewById(R.id.submit)).setText(label);
    }

    protected void setTitle(String title) {
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    protected Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

    protected ViewPager getPager() {
        return (ViewPager) findViewById(R.id.pager);
    }

    protected abstract void initializeView();

    protected abstract void initializeLabels() throws JSONException;

    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        this.formSections = getContext().getFormSections();

        if (savedInstanceState != null && savedInstanceState.containsKey("child_state")) {
            this.child = new Child(savedInstanceState.getString("child_state"));
        } else if (child == null) {
            child = new Child();
        }
    }

    protected void initializePager() {
        getPager().setAdapter(new FormSectionPagerAdapter(formSections, child, editable));
        getPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSpinner().setSelection(position);
            }

        });
    }

    protected void initializeSpinner() {
        getSpinner().setAdapter(new ArrayAdapter<FormSection>(this, android.R.layout.simple_spinner_item, formSections));
        getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPager().setCurrentItem(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

        child.setOwner(getContext().getUserName());
        child.generateUniqueId();

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
        startActivity(intent);
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
                view();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
