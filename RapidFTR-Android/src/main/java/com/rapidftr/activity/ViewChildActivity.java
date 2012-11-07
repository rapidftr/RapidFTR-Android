package com.rapidftr.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.repository.ChildRepository;
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

        @Cleanup ChildRepository repository = getInjector().getInstance(ChildRepository.class);
        String childId = getIntent().getExtras().getString("id");

        try {
            child = repository.get(childId);
            if (child == null) throw new NullPointerException();

            ((TextView) findViewById(R.id.title)).setText(child.getId());
        } catch (Exception e) {
            makeToast(R.string.internal_error);
        }
    }

}
