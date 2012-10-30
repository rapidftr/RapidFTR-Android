package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.rapidftr.view.fields.BaseView;

public class FormSectionView extends ScrollView {

    private FormSection formSection;

    private Child child;

    public FormSectionView(Context context) {
        super(context);
    }

    public FormSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FormSectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected TextView getLabel() {
        return (TextView) findViewById(R.id.label);
    }

    protected TextView getHelpText() {
        return (TextView) findViewById(R.id.help_text);
    }

    protected LinearLayout getContainer() {
        return (LinearLayout) findViewById(R.id.container);
    }

    public void initialize(FormSection formSection, Child child) {
        if (this.formSection != null)
            throw new IllegalArgumentException("Form section is already initialized!");

        this.formSection = formSection;
        this.child = child;
        this.initialize();
    }

    protected void initialize() {
        getLabel().setText(formSection.getName());
        getHelpText().setText(formSection.getHelpText());
        for (FormField field : formSection.getFields()) {
            BaseView fieldView = createFormField(field);
            if (fieldView != null)
                getContainer().addView(fieldView);
        }
    }

    protected int getFieldLayoutId(String fieldType) {
        return getResources().getIdentifier("form_" + fieldType, "layout", getContext().getPackageName());
    }

    protected BaseView createFormField(FormField field) {
        int resourceId = getFieldLayoutId(field.getType());

        if (resourceId > 0) {
            BaseView fieldView = (BaseView) LayoutInflater.from(getContext()).inflate(resourceId, null);
            fieldView.initialize(field, child);
            return fieldView;
        }

        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        LinearLayout container = getContainer();
        for (int i=0, j=container.getChildCount(); i<j; i++)
            container.getChildAt(i).setEnabled(enabled);
    }
}
