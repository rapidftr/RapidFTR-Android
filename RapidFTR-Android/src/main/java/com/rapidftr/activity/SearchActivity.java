package com.rapidftr.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.FormService;
import lombok.Cleanup;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends RapidFtrActivity {

    private HighlightedFieldsViewAdapter highlightedFieldsViewAdapter;

    private FormService formService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_child);
        findViewById(R.id.search_btn).setOnClickListener(searchListener());
        formService = inject(FormService.class);
    }

    private void listView(List<Child> children) {
        highlightedFieldsViewAdapter = new HighlightedFieldsViewAdapter(this, R.layout.row_child, children, Child.CHILD_FORM_NAME, ViewChildActivity.class);
        ListView childListView = (ListView) findViewById(R.id.child_list);
        if (children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_child_view));
        }
        childListView.setAdapter(highlightedFieldsViewAdapter);
    }

    private View.OnClickListener searchListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView searchTextView = (TextView) findViewById(R.id.search_text);
                String subString = searchTextView.getText().toString();
                try {
                    listView(search(subString));
                } catch (Exception e) {
                    Log.e("ChildSearch", "Error while Searching Children");
                    makeToast(R.string.fetch_child_error);
                }
            }
        };
    }

    private List<Child> search(String subString) throws JSONException {
        @Cleanup ChildRepository childRepository = inject(ChildRepository.class);
        subString = subString.trim();
        if ("".equals(subString)) {
            return new ArrayList<Child>();
        }
        return childRepository.getMatchingChildren(subString, formService.getHighlightedFields(Child.CHILD_FORM_NAME));
    }

}
