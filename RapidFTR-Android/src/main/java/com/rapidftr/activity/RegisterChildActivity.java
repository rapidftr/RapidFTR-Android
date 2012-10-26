package com.rapidftr.activity;

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
import com.rapidftr.database.ChildDAO;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.rapidftr.task.SaveChildAsyncTask;
import com.rapidftr.view.FormSectionView;
import lombok.Cleanup;

import java.util.List;

public class RegisterChildActivity extends RapidFtrActivity {

    protected List<FormSection> formSections = null;

    protected Child child;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_child);

        this.formSections = getContext().getFormSections();
        this.child        = new Child();

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                child.setOwner(RapidFtrApplication.getInstance().getUserName());
                child.generateUniqueId();

                @Cleanup ChildDAO dao = getInjector().getInstance(ChildDAO.class);
                new SaveChildAsyncTask(dao, getContext()).execute(child);
            } catch (Exception e) {
                makeToast(R.string.internal_error);
            }
            }
        });

        initializePager();
        initializeSpinner();
    }

    protected Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

    protected ViewPager getPager() {
        return (ViewPager) findViewById(R.id.pager);
    }

    protected void initializePager() {
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

    protected void initializeSpinner() {
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

    protected class FormSectionPagerAdapter extends PagerAdapter {
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
            FormSection section = formSections.get(position);
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
