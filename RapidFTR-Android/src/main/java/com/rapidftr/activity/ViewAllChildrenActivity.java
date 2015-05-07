package com.rapidftr.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.adapter.pagination.ViewAllChildrenPaginatedScrollListener;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ViewAllChildrenActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_children);

        try {
            super.hideEnquiriesTabIfRapidReg();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView(getChildren());
    }

    private List<Child> getChildren() {
        List<Child> children = new ArrayList<Child>();
        @Cleanup ChildRepository childRepository = inject(ChildRepository.class);
        try {
            children = childRepository.getRecordsForFirstPage();
        } catch (JSONException e) {
            Log.e("ViewAllChildrenActivity", "Error while displaying children list");
            makeToast(R.string.fetch_child_error);
        }
        return children;
    }

    private void listView(List<Child> children) {
        HighlightedFieldsViewAdapter highlightedFieldsViewAdapter = new HighlightedFieldsViewAdapter(this, children, Child.CHILD_FORM_NAME, ViewChildActivity.class);
        ListView childListView = (ListView) findViewById(R.id.child_list);
        if (children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_child_view));
        }
        childListView.setAdapter(highlightedFieldsViewAdapter);
        ViewAllChildrenPaginatedScrollListener scrollListener = new ViewAllChildrenPaginatedScrollListener(inject(ChildRepository.class), highlightedFieldsViewAdapter);
        childListView.setOnScrollListener(scrollListener);
    }
}
