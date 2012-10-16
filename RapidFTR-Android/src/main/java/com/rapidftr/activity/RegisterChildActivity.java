package com.rapidftr.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormSection;
import com.rapidftr.view.FormSectionView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterChildActivity extends RapidFtrActivity {

    private Map<String, FormSectionView> views = new HashMap<String, FormSectionView>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_child);
        try {
            setFormSectionSelectionListener();
            populateDropDown(RapidFtrApplication.getChildFormSections());
        } catch (Exception ex) {
            logError(ex.getMessage());
        }
    }

    private void displayFormSection(FormSection section) {
        FormSectionView view = views.get(section.getName());
        if (view == null) {
            view = (FormSectionView) LayoutInflater.from(this).inflate(R.layout.form_section, null);
            view.setFormSection(section);
            views.put(section.getName(), view);

        }

        LinearLayout detailsView = (LinearLayout) this.findViewById(R.id.details);
        detailsView.removeAllViews();
        detailsView.addView(view);

    }


    private void setFormSectionSelectionListener() {
        getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int arg2, long arg3) {
                displayFormSection((FormSection) getSpinner().getSelectedItem());
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        }
        );
    }

    private void populateDropDown(List<FormSection> formSections) {
        ArrayAdapter<FormSection> childDetailsFormArrayAdapter = new ArrayAdapter<FormSection>(this, android.R.layout.simple_spinner_item, formSections);
        getSpinner().setAdapter(childDetailsFormArrayAdapter);
    }

    private Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

}

