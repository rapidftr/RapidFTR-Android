package com.rapidftr.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.dao.ChildDAO;
import com.rapidftr.view.FormSectionView;
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
        formSections = getContext().getFormSections();

        @Cleanup ChildDAO dao = getInjector().getInstance(ChildDAO.class);
        String childId = getIntent().getExtras().getString("id");

        try {
            child = dao.get(childId);
            if (child == null) throw new NullPointerException();

            ((TextView) findViewById(R.id.title)).setText(child.getId());
        } catch (Exception e) {
            makeToast(R.string.internal_error);
        }
    }

    @Override
    protected FormSectionView createFormSectionView(int position) {
        FormSectionView view = super.createFormSectionView(position);
        view.setEnabled(false);
        return view;
    }

}
