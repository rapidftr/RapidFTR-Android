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
import com.rapidftr.model.BaseModel;
import com.rapidftr.view.fields.BaseView;

public class DefaultFormSectionView extends ScrollView implements FormSectionView {

    private FormSection formSection;

    private BaseModel model;

    public DefaultFormSectionView(Context context) {
        super(context);
        inflateView(context);
    }

    public DefaultFormSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public DefaultFormSectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflateView(context);
    }


    private void inflateView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.form_section, this);
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

    public void initialize(FormSection formSection, BaseModel model) {
        if (this.formSection != null)
            throw new IllegalArgumentException("Form section is already initialized!");

        this.formSection = formSection;
        this.model = model;
        this.initialize();
    }

    protected void initialize() {
        getLabel().setText(formSection.getLocalizedName());
        getHelpText().setText(formSection.getLocalizedHelpText());
        for (FormField field : formSection.getFields()) {
            BaseView fieldView = createFormField(field);
            if (fieldView != null)
                getContainer().addView(fieldView);
        }
    }

    protected int getFieldLayoutId(String fieldType) {
        return getResources().getIdentifier("form_" + fieldType, "layout", "com.rapidftr");
    }

    protected BaseView createFormField(FormField field) {
        int resourceId = getFieldLayoutId(field.getType());

        if (resourceId > 0) {
            BaseView fieldView = (BaseView) LayoutInflater.from(getContext()).inflate(resourceId, null);
            fieldView.initialize(field, model);

            return fieldView;
        }

        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        LinearLayout container = getContainer();
        for (int i = 0, j = container.getChildCount(); i < j; i++)
            container.getChildAt(i).setEnabled(enabled);
    }
}
