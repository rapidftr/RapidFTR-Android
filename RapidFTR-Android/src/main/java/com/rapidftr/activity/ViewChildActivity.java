package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        setContentView(R.layout.activity_register_child);
        initializeData();
        initializePager();
        initializeSpinner();
        initializeListeners();
        setLabel(R.string.edit);
        setTitle();
    }

    @Override
    protected void initializeData() {
        editable = false;
        formSections = getContext().getFormSections();

        @Cleanup ChildRepository repository = getInjector().getInstance(ChildRepository.class);
        String childId = getIntent().getExtras().getString("id");

        try {
            child = repository.get(childId);
            ((TextView) findViewById(R.id.title)).setText(child.getUniqueId());
        } catch (Exception e) {
            makeToast(R.string.internal_error);
        }
    }

    @Override
    protected void initializeListeners(){
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editChild();
            }
        });
    }

    protected void editChild() {
        Intent registerChildActivity = new Intent(this, RegisterChildActivity.class);
        registerChildActivity.putExtra("child", child);
        startActivity(registerChildActivity);
    }

}
