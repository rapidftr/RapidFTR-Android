package com.rapidftr.activity;

import android.os.Bundle;
import com.google.common.io.CharStreams;
import com.rapidftr.R;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Enquiry;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateEnquiryActivity extends BaseEnquiryActivity {
    public void initializeView() {
        setContentView(R.layout.activity_create_enquiry);
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        if(formSections.get(0).getName().containsValue("Potential matches"))
            formSections.remove(0);
    }
}
