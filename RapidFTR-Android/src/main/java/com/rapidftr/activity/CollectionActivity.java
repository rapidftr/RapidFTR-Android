package com.rapidftr.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.*;
import com.rapidftr.R;
import com.rapidftr.adapter.FormSectionPagerAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public abstract class CollectionActivity extends RapidFtrActivity {
    protected FormService formService;

    protected List<FormSection> formSections;

    protected abstract BaseModel getModel();

    protected abstract Boolean getEditable();

    protected Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

    protected ViewPager getPager() {
        return (ViewPager) findViewById(R.id.pager);
    }

    protected CollectionActivity() {
        super();
    }

    protected void initializePager() {
        getPager().setAdapter(new FormSectionPagerAdapter(formSections, getModel(), getEditable()));
        getPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSpinner().setSelection(position);
            }

        });
    }

    protected void initializeSpinner() {
        getSpinner().setAdapter(new ArrayAdapter<FormSection>(this, android.R.layout.simple_spinner_item, formSections));
        getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPager().setCurrentItem(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            formService = inject(FormService.class);
            initializeView();

            try {
                initializeData(savedInstanceState);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initializePager();
            initializeSpinner();
            initializeLabels();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initializeLabels() throws JSONException {
    }

    ;

    protected abstract void initializeView();

    protected abstract void initializeData(Bundle savedInstanceState) throws JSONException, IOException;

    protected void setLabel(int label) {
        ((Button) findViewById(R.id.submit)).setText(label);
    }

    protected void setTitle(String title) {
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    protected FormService getFormService() {
        if (this.formService == null)
            formService = inject(FormService.class);

        return formService;
    }
}
