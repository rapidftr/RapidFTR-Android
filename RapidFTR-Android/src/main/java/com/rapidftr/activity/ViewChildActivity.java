package com.rapidftr.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.dao.ChildRepoistory;
import lombok.Cleanup;

public class ViewChildActivity extends RegisterChildActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initialize() {
        setContentView(R.layout.activity_view_child);

        initializeData();
        initializePager();
        initializeSpinner();
    }

    @Override
    protected void initializeData() {
        editable = false;
        formSections = getContext().getFormSections();

        @Cleanup ChildRepoistory repoistory = getInjector().getInstance(ChildRepoistory.class);
        String childId = getIntent().getExtras().getString("id");

        try {
            child = repoistory.get(childId);
            if (child == null) throw new NullPointerException();

            ((TextView) findViewById(R.id.title)).setText(child.getId());
        } catch (Exception e) {
            makeToast(R.string.internal_error);
        }
    }

}
