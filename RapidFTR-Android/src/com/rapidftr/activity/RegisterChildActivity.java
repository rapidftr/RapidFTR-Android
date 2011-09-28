package com.rapidftr.activity;

import android.os.Bundle;
import android.text.AndroidCharacter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.ChildDetailsForm;
import com.rapidftr.view.CameraPreview;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegisterChildActivity extends RapidFtrActivity {

    private List<ChildDetailsForm> formSections;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_child);
        try{
            formSections = RapidFtrApplication.getChildFormSections();
            populateDropDown(formSections);
            loge(String.valueOf(formSections.size()));
        }
        catch (Exception ex){
            String str = ex.getMessage();
        }
    }

    private void populateDropDown(List<ChildDetailsForm> formSections) {
        ArrayAdapter<ChildDetailsForm> childDetailsFormArrayAdapter = new ArrayAdapter<ChildDetailsForm>(this, android.R.layout.simple_spinner_item, formSections);
        ((Spinner) findViewById(R.id.spinner)).setAdapter(childDetailsFormArrayAdapter);
    }
}

