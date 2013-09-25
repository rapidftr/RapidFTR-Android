package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.IOException;

public class ViewEnquiryActivity extends BaseEnquiryActivity{

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_view_enquiry);
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(enquiry.getShortId());
    }

    private Enquiry load() throws JSONException {
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        String enquiryId = getIntent().getExtras().getString("id");
        enquiry = repository.get(enquiryId);
        return enquiry;
    }
}
