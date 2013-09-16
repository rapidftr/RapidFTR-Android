package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.Cleanup;
import org.json.JSONException;

public abstract class BaseEnquiryActivity extends RapidFtrActivity {
    protected Enquiry enquiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initializeView();
            initializeData(savedInstanceState);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void initializeView();

    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        enquiry = new Enquiry();
    }

    public Enquiry save(){
        AsyncTaskWithDialog.wrap(this, new SaveEnquiryTask(), R.string.save_enquiry_progress, R.string.save_enqury_success, R.string.save_enquiry_failed).execute();
        return enquiry;
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
    }

    private Enquiry saveEnquiry() throws JSONException {
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        repository.createOrUpdate(enquiry);
        return enquiry;
    }
}
