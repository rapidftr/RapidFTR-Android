package com.rapidftr.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.common.io.CharStreams;
import com.rapidftr.R;
import com.rapidftr.adapter.FormSectionPagerAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.Cleanup;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public abstract class BaseEnquiryActivity extends RapidFtrActivity {
    protected Enquiry enquiry;
    protected List<FormSection> formSections;
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void initializeView();

    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        enquiry = new Enquiry();
        @Cleanup InputStream in = getResources().openRawResource(R.raw.enquiry_form_sections);
        String x = CharStreams.toString(new InputStreamReader(in));
        formSections = Arrays.asList(JSON_MAPPER.readValue(x, FormSection[].class));
    }

    protected Spinner getSpinner() {
        return ((Spinner) findViewById(R.id.spinner));
    }

    protected ViewPager getPager() {
        return (ViewPager) findViewById(R.id.pager);
    }

    protected void initializePager() {
        getPager().setAdapter(new FormSectionPagerAdapter(formSections, enquiry, true));
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


    public Enquiry save(){
        AsyncTaskWithDialog.wrap(this, new SaveEnquiryTask(), R.string.save_enquiry_progress, R.string.save_enqury_success, R.string.save_enquiry_failed).execute();
        return enquiry;
    }

    private class SaveEnquiryTask extends AsyncTaskWithDialog<Void, Void, Enquiry> {
        @Override
        public void cancel() {
            this.cancel(false);
        }

        @Override
        protected Enquiry doInBackground(Void... params) {
            try{
                return saveEnquiry();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    private Enquiry saveEnquiry() throws JSONException {
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        if (enquiry.isNew()) {
            enquiry.setOwner(getCurrentUser().getUserName());
            enquiry.setOrganisation(getCurrentUser().getOrganisation());
        }
        repository.createOrUpdate(enquiry);
        return enquiry;
    }
}
