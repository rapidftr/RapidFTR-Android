package com.rapidftr.activity;

import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.View;
import android.widget.*;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.ChildDetailsForm;
import com.rapidftr.forms.FormSectionViewBuilder;
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
            setFormSectionSelectionListener();
            populateDropDown(formSections);
            loge(String.valueOf(formSections.size()));
        }
        catch (Exception ex){
            String str = ex.getMessage();
        }
    }

    private void displayFormSection(ChildDetailsForm section){
        ScrollView view = new FormSectionViewBuilder(this).with(section).build();
        LinearLayout byId = (LinearLayout) this.findViewById(R.id.details);
        byId.removeAllViews();
        byId.addView(view);

    }

    private void setFormSectionSelectionListener() {
        Spinner spinner = ((Spinner) findViewById(R.id.spinner));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0,
            View arg1, int arg2, long arg3)
            {
                Spinner spinner = ((Spinner) findViewById(R.id.spinner));
                displayFormSection((ChildDetailsForm)spinner.getSelectedItem());
            }

            public void onNothingSelected(AdapterView<?> arg0) {}
        }
        );
    }

    private void populateDropDown(List<ChildDetailsForm> formSections) {
        ArrayAdapter<ChildDetailsForm> childDetailsFormArrayAdapter = new ArrayAdapter<ChildDetailsForm>(this, android.R.layout.simple_spinner_item, formSections);
        Spinner spinner = ((Spinner) findViewById(R.id.spinner));
        spinner.setAdapter(childDetailsFormArrayAdapter);
    }

}

