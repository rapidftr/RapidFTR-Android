package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;
import com.rapidftr.dao.ChildDAO;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.rapidftr.view.FormSectionView;
import lombok.Cleanup;

import java.util.List;

public class ViewChildActivity extends RegisterChildActivity {

    protected List<FormSection> formSections = null;

    protected Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeData() {
        formSections = getContext().getFormSections();

        @Cleanup ChildDAO dao = getInjector().getInstance(ChildDAO.class);
        String childId = getIntent().getExtras().getString("id");

        try {
            child = dao.get(childId);
            if (child == null) throw new NullPointerException();
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

    @Override
    protected void initializeListeners() {
        // No-op
    }
}
