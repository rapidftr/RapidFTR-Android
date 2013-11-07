package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
        String formSectionJSON = CharStreams.toString(new InputStreamReader(in));
        formSections = new ArrayList<FormSection>(Arrays.asList(JSON_MAPPER.readValue(formSectionJSON, FormSection[].class)));
    }

    protected Enquiry load(Bundle bundle, EnquiryRepository enquiryRepository) throws JSONException {
        String enquiryId = bundle.getString("id");
        Enquiry enquiry1 = enquiryRepository.get(enquiryId);
        JSONObject criteria = (JSONObject) enquiry1.remove("criteria");
        for(String key: JSONObject.getNames(criteria)){
            enquiry1.put(key, criteria.getString(key));
        }
        return enquiry1;
    }

    public Enquiry save(View view){
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
