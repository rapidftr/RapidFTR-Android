package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
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
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }
    
    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        enquiry = new Enquiry();
        @Cleanup InputStream in = getResources().openRawResource(R.raw.enquiry_form_sections);
        String x = CharStreams.toString(new InputStreamReader(in));
        formSections = new ArrayList<FormSection>(Arrays.asList(JSON_MAPPER.readValue(x, FormSection[].class)));
        int indexOfPotentialMatches = formSections.size() - 1;

        formSections.remove(indexOfPotentialMatches);
    }
}
