package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.rapidftr.R;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.IOException;

public abstract class BaseEnquiryActivity extends CollectionActivity {
    protected Enquiry enquiry;
    protected EnquiryRepository enquiryRepository;
    protected boolean editable = true;

    @Override
    protected Boolean getEditable() {
        return editable;
    }

    @Override
    protected BaseModel getModel() {
        return enquiry;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        enquiryRepository = inject(EnquiryRepository.class);
        super.onCreate(savedInstanceState);
    }

    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        enquiry = new Enquiry();
        formSections = getFormService().getFormSections(Enquiry.ENQUIRY_FORM_NAME);
    }

    protected Enquiry loadEnquiry(Bundle bundle, EnquiryRepository enquiryRepository) throws JSONException {
        String enquiryId = bundle.getString("id");
        Enquiry retrievedEnquiry = enquiryRepository.get(enquiryId);
        enquiryRepository.close();

        return retrievedEnquiry;
    }

    public Enquiry save(View view) {
        if (enquiry.isValid()) {
            AsyncTaskWithDialog.wrap(this, new SaveEnquiryTask(), R.string.save_enquiry_progress, R.string.save_enqury_success, R.string.save_enquiry_failed).execute();
            return enquiry;
        } else {
            makeToast(R.string.save_enquiry_invalid);
            return null;
        }
    }

    private class SaveEnquiryTask extends AsyncTaskWithDialog<Void, Void, Enquiry> {
        @Override
        public void cancel() {
            this.cancel(false);
        }

        @Override
        protected Enquiry doInBackground(Void... params) {
            try {
                return saveEnquiry();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Enquiry result) {
            try {
                if (result != null)
                    view();
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void view() throws JSONException {
        Intent intent = new Intent(this, ViewEnquiryActivity.class);
        intent.putExtra("id", enquiry.getUniqueId());
        finish();
        startActivity(intent);
    }

    private Enquiry saveEnquiry() throws Exception {
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        if (enquiry.isNew()) {
            enquiry.setCreatedBy(getCurrentUser().getUserName());
            enquiry.setOrganisation(getCurrentUser().getOrganisation());
        }
        repository.createOrUpdate(enquiry);
        return enquiry;
    }
}
