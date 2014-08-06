package com.rapidftr.activity;

import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.adapter.EnquiryViewAdapter;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import lombok.Cleanup;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewAllEnquiryActivity extends BaseEnquiryActivity {
    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_view_all_enquiries);
        @Cleanup EnquiryRepository enquiryRepository = inject(EnquiryRepository.class);
        try {
            List<Enquiry> enquiries = enquiryRepository.all();
            listView(enquiries);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void listView(List<Enquiry> enquiries) {
        EnquiryViewAdapter enquiryViewAdapter = new EnquiryViewAdapter(this, R.layout.row_enquiry, enquiries);
        ListView enquiryListView = (ListView) findViewById(R.id.enquiry_list);
        if (enquiries.isEmpty()) {
            enquiryListView.setEmptyView(findViewById(R.id.no_enquiry_view));
        }
        enquiryListView.setAdapter(enquiryViewAdapter);
    }

    @Override
    protected void initializePager() {
    }

    @Override
    protected void initializeSpinner() {
    }
}
