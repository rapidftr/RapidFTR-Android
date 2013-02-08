package com.rapidftr.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
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
        menuId = R.menu.view_children_menu;
        listView(getChildren());
    }
    
    private List<Child> getChildren()
    {
        List<Child> children = new ArrayList<Child>();
        @Cleanup ChildRepository childRepository = inject(ChildRepository.class);
        try {
            children = childRepository.getChildrenByOwner();
        } catch (JSONException e) {
            Log.e("ViewAllChildrenActivity","Error while displaying children list");
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
