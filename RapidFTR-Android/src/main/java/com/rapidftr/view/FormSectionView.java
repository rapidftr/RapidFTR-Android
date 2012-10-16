package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.rapidftr.R;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.FormField;
import com.rapidftr.view.fields.BaseView;

public class FormSectionView extends ScrollView {

    private FormSection formSection;

    public FormSectionView(Context context) {
        super(context);
    }

    public FormSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FormSectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected LinearLayout getContainer() {
        return (LinearLayout) findViewById(R.id.container);
    }

    public FormSection getFormSection() {
        return formSection;
    }

    public void setFormSection(FormSection formSection) {
        this.formSection = formSection;
        this.initialize();
    }

    protected void initialize() {
        for (FormField field : formSection.getFields())
            addFormField(field);
    }

    protected void addFormField(FormField field) {
        int resourceId = getResources().getIdentifier(field.getType(), "layout", getContext().getPackageName());
        if (resourceId > 0) {
            BaseView fieldView = (BaseView) LayoutInflater.from(getContext()).inflate(resourceId, null);
            fieldView.setFormField(field);
            getContainer().addView(fieldView);
        }
    }

}
