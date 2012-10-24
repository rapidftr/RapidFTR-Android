package com.rapidftr.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.rapidftr.view.FormSectionView;
import org.json.JSONObject;

import java.util.List;

public class RegisterChildActivity extends RapidFtrActivity {

    private List<FormSection> formSections = null;

    private JSONObject child = new JSONObject();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_child);
        try {
            this.formSections = RapidFtrApplication.getChildFormSections();
        } catch (Exception ex) {
            logError(ex.getMessage());
        }
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences rapidftrPreferences = getApplication().getSharedPreferences(RapidFtrActivity.SHARED_PREFERENCES_FILE,0);
                String username = rapidftrPreferences.getString("USER_NAME","");
                Child.save(child, username);
            }
        });
        initializePager();
        initializeSpinner();
    }

    private Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

    private ViewPager getPager() {
        return (ViewPager) findViewById(R.id.pager);
    }

    private void initializePager() {
        FormSectionPagerAdapter formSectionAdapter = new FormSectionPagerAdapter();
        getPager().setAdapter(formSectionAdapter);
        getPager().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int i, float v, int i1) { }
            public void onPageScrollStateChanged(int i) { }

            @Override
            public void onPageSelected(int position) {
                getSpinner().setSelection(position);
            }

        });
    }

    private void initializeSpinner() {
        ArrayAdapter<FormSection> childDetailsFormArrayAdapter = new ArrayAdapter<FormSection>(this, android.R.layout.simple_spinner_item , formSections);
        getSpinner().setAdapter(childDetailsFormArrayAdapter);
        getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPager().setCurrentItem(position);
            }

            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }



    private class FormSectionPagerAdapter extends PagerAdapter {

        FormSection section;
        @Override
        public int getCount() {
            return formSections.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FormSectionView view = (FormSectionView) LayoutInflater.from(RegisterChildActivity.this).inflate(R.layout.form_section, null);
            section = formSections.get(position);
            view.setFormSection(section, child);
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
