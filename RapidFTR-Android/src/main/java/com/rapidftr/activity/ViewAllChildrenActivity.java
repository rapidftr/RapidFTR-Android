package com.rapidftr.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewAllChildrenActivity extends RapidFtrActivity {

    @Getter @Setter List<Child> children;
    @Getter @Setter ChildViewAdapter childViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_children);
        menuId = R.menu.view_children_menu;
        this.children = getChildren();
        listView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_by) {
           showSortOptions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortOptions() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                 switch (item){
                     case 0:
                         sortChildrenByName();
                         break;
                     case 1:
                         sortChildrenByRecentUpdate();
                         break;
                 }
            }
        };
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.sort_by_options)).setCancelable(false);
        alertDialog.setItems(new String[]{getString(R.string.sort_by_name), getString(R.string.sort_by_recent_update)}, listener);
        alertDialog.create().show();
    }

    public void sortChildrenByRecentUpdate() {
        Collections.sort(this.children, new Comparator<Child>() {
            @Override
            public int compare(Child child, Child child1) {
               Timestamp childLastUpdateAt = Timestamp.valueOf(child.getLastUpdatedAt());
               Timestamp child1LastUpdateAt = Timestamp.valueOf(child1.getLastUpdatedAt());
               return child1LastUpdateAt.compareTo(childLastUpdateAt);
            }
        });
        childViewAdapter.notifyDataSetChanged();
    }

    public void sortChildrenByName() {
        Collections.sort(this.children, new Comparator<Child>() {
            @Override
            public int compare(Child child, Child child1) {
                return child.getName().compareTo(child1.getName());
            }
        });
        childViewAdapter.notifyDataSetChanged();
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
    
    private void listView() {
        childViewAdapter = new ChildViewAdapter(this, R.layout.row_child, this.children);

        ListView childListView = (ListView) findViewById(R.id.child_list);
        if (this.children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_child_view));
        }
        childListView.setAdapter(childViewAdapter);
    }
}
