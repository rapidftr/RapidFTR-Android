package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewEnquiryActivity extends BaseEnquiryActivity {

    @Override
    protected void initializeView() {
        Log.e("Check","Setting the content view" );
        setContentView(R.layout.activity_view_enquiry);
        Log.e("Check","Setting the content view DONE" );
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
        listingAllViewsInCurrentActivity();
    }

    private void listingAllViewsInCurrentActivity() {


    }

    private void loadPotentialMatches() throws JSONException {
        @Cleanup ChildRepository childRepository = inject(ChildRepository.class);
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList(enquiry.getPotentialMatches()));
        List<Child> children = childRepository.getChildrenByIds(strings);

         ChildViewAdapter childViewAdapter;

        childViewAdapter = new ChildViewAdapter(this, R.layout.row_child, children);

        TextView text = (TextView) findViewById(R.id.text);
        ListView childListView = (ListView) findViewById(R.id.list_records);
        if (children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_matches));
        }

        childListView.setAdapter(childViewAdapter);
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(enquiry.getShortId());
        loadPotentialMatches();
    }

    public void edit(View view) throws JSONException {
        Intent editEnquiryIntent = new Intent(this, EditEnquiryActivity.class);
        editEnquiryIntent.putExtra("id", enquiry.getUniqueId());
        startActivity(editEnquiryIntent);
    }

}
