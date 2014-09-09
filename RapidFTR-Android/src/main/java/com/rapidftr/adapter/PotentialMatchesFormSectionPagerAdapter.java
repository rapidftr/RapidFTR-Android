package com.rapidftr.adapter;

import android.view.View;
import android.view.ViewGroup;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.PotentialMatchesFormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.view.FormSectionView;
import com.rapidftr.view.PotentialMatchesFormSectionView;

import java.util.List;

public class PotentialMatchesFormSectionPagerAdapter extends FormSectionPagerAdapter {

    private FormSectionView potentialMatchesView;

    public PotentialMatchesFormSectionPagerAdapter(List<FormSection> formSections, BaseModel baseModel, boolean editable, FormSectionView potentialMatchesView) {
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
        FormSectionView view;

        if (formSection instanceof PotentialMatchesFormSection) {
            view = potentialMatchesView;
        } else {
            view = createFormSectionView(container);
        }

        view.initialize(formSection, baseModel);
        view.setEnabled(editable);
        container.addView(view, 0);
        return view;
    }
}
