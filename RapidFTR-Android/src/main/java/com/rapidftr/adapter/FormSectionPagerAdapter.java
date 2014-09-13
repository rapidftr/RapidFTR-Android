package com.rapidftr.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.view.DefaultFormSectionView;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(suppressConstructorProperties = true)
public class FormSectionPagerAdapter extends PagerAdapter {

    protected List<FormSection> formSections;
    protected BaseModel baseModel;
    protected boolean editable;

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
        DefaultFormSectionView view = createFormSectionView(container);
        view.initialize(formSections.get(position), baseModel);
        view.setEnabled(editable);
        container.addView(view, 0);
        return view;
    }

    protected DefaultFormSectionView createFormSectionView(ViewGroup container) {
        return new DefaultFormSectionView(container.getContext());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean equals(Object other) {
        return (other == null || !(other instanceof FormSectionPagerAdapter)) ? false : equals((FormSectionPagerAdapter) other);

    }

    public boolean equals(FormSectionPagerAdapter that) {
        return this.editable == that.editable && this.formSections.equals(that.formSections) && this.baseModel.equals(that.baseModel);
    }

}
