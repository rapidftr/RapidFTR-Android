package com.rapidftr.adapter;

import android.view.View;
import android.view.ViewGroup;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.PotentialMatchesFormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.view.DefaultFormSectionView;
import com.rapidftr.view.PotentialMatchesFormSectionView;

import java.util.List;

public class PotentialMatchesFormSectionPagerAdapter extends FormSectionPagerAdapter {

    private PotentialMatchesFormSectionView potentialMatchesView;

    public PotentialMatchesFormSectionPagerAdapter(List<FormSection> formSections, BaseModel baseModel, boolean editable,
                                                   PotentialMatchesFormSectionView potentialMatchesView) {
        super(formSections, baseModel, editable);
        this.potentialMatchesView = potentialMatchesView;
    }

    @Override
    public int getCount() {
        return formSections.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FormSection formSection = formSections.get(position);
        View view = null;

        if (formSection instanceof PotentialMatchesFormSection) {
            potentialMatchesView.initialize(formSection, baseModel);
            view = potentialMatchesView;
        } else {
            DefaultFormSectionView defaultFormSectionView = createFormSectionView(container);
            defaultFormSectionView.initialize(formSection, baseModel);
            view = defaultFormSectionView;
        }

        view.setEnabled(editable);
        container.addView(view, 0);
        return view;
    }
}
