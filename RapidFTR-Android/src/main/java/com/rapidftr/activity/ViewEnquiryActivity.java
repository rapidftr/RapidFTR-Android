package com.rapidftr.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewEnquiryActivity extends BaseEnquiryActivity{

    protected Enquiry enquiry;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_view_enquiry);
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
        listView(getChildren());
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(enquiry.getShortId());
    }

    private void load() throws JSONException {
        @Cleanup EnquiryRepository repository = inject(EnquiryRepository.class);
        String enquiryId = getIntent().getExtras().getString("id");
        enquiry = repository.get(enquiryId);
    }

    private List<Child> getChildren() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        @Cleanup ChildRepository childRepository = inject(ChildRepository.class);
        try {
            children = childRepository.getChildrenByIds(enquiry.getPotentialMatches());
        } catch (JSONException e) {
            Log.e("ViewAllChildrenActivity", "Error while displaying children list");
            makeToast(R.string.fetch_child_error);
        }
        return children;
    }

    private void listView(List<Child> children) {
        ChildViewAdapter childViewAdapter = new ChildViewAdapter(this, R.layout.row_child, children);
        ListView childListView = (ListView) findViewById(R.id.child_list);
        if (children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_child_view));
        }
        childListView.setAdapter(childViewAdapter);
    }
}
