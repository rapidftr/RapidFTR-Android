package com.rapidftr.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rapidftr.R;
import com.rapidftr.adapter.FormSectionPagerAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.BaseModel;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public abstract class CollectionActivity extends RapidFtrActivity {
    protected List<FormSection> formSections;

    protected abstract BaseModel getModel();
    protected abstract Boolean getEditable();

    protected Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

    protected ViewPager getPager() {
        return (ViewPager) findViewById(R.id.pager);
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
            initializeView();
            try {
                initializeData(savedInstanceState);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            initializePager();
            initializeSpinner();
            initializeLabels();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    protected void initializeLabels() throws JSONException{};

    protected abstract void initializeView();
    protected abstract void initializeData(Bundle savedInstanceState) throws JSONException, IOException;
}
