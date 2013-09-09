package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.IOException;

public class RegisterEnquiryActivity extends RapidFtrActivity {
    
    protected Enquiry enquiry; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_enquiry);
    }

    protected void saveEnquiry() throws JSONException,IOException {

       //TODO must validate. DO it on the UI
        if (enquiry.isNew()) {
            enquiry.setOwner(getCurrentUser().getUserName());
        }

        enquiry.generateUniqueId();
        enquiry.setSynced(false);
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        repository.createOrUpdate(enquiry);
        this.finish();
    }
}
