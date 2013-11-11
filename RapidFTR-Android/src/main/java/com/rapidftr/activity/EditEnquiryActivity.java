package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;
import org.json.JSONException;

import java.io.IOException;

public class EditEnquiryActivity extends BaseEnquiryActivity{

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_create_enquiry);
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        if(formSections.get(0).getName().containsValue("Potential matches"))
            formSections.remove(0);
        this.editable = true;
        this.enquiry = loadEnquiry(getIntent().getExtras(), this.enquiryRepository);
    }
}
