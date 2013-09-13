package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;

public abstract class BaseEnquiryActivity extends RapidFtrActivity {
    protected Enquiry enquiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    protected abstract void initializeView();

    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        enquiry = new Enquiry();
    }

    public Enquiry save(){
        return enquiry;
    }
}
