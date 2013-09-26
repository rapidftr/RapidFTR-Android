package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.common.io.CharStreams;
import com.rapidftr.R;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.Cleanup;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public abstract class BaseEnquiryActivity extends CollectionActivity {
    protected Enquiry enquiry;
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    protected boolean editable = true;

    @Override
    protected Boolean getEditable() {
        return editable;
    }

    @Override
    protected BaseModel getModel() {
        return enquiry;
    }


    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        enquiry = new Enquiry();
        @Cleanup InputStream in = getResources().openRawResource(R.raw.enquiry_form_sections);
        String x = CharStreams.toString(new InputStreamReader(in));
        formSections = Arrays.asList(JSON_MAPPER.readValue(x, FormSection[].class));
    }


    public Enquiry save(){
        if ( enquiry.isValid()){
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
            try{
                return saveEnquiry();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Enquiry result) {
            if (result != null)
                view();
        }
    }

    private void view() {
        Intent intent = new Intent(this, CreateEnquiryActivity.class);
        finish();
        startActivity(intent);
    }

    private Enquiry saveEnquiry() throws JSONException {
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        if (enquiry.isNew()) {
            enquiry.setOwner(getCurrentUser().getUserName());
            enquiry.setOrganisation(getCurrentUser().getOrganisation());
        }
        repository.createOrUpdate(enquiry);
        return enquiry;
    }
}
