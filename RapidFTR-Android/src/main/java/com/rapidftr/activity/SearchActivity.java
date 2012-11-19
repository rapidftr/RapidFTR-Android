package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends RapidFtrActivity {

    private ChildViewAdapter childViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_child);
        findViewById(R.id.search_btn).setOnClickListener(searchListener());
    }

    private void listView(List<Child> children) {
        childViewAdapter = new ChildViewAdapter(this, R.layout.row_child, children);
        ListView childListView = (ListView) findViewById(R.id.child_list);
        if (children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_child_view));
        }
        childListView.setAdapter(childViewAdapter);
    }

    private View.OnClickListener searchListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView searchTextView = (TextView) findViewById(R.id.search_text);
                String subString = searchTextView.getText().toString();
                listView(search(subString));
            }
        };
    }

    private List<Child> search(String subString) {
        ChildService childService = getInjector().getInstance(ChildService.class);
        subString = subString.trim();
        if ("".equals(subString)) {
            return new ArrayList<Child>();
        }
        return childService.searchChildrenInDB(subString);
    }

}
