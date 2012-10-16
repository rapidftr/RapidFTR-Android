package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.forms.FormField;

public abstract class BaseView extends LinearLayout {

    protected FormField formField;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFormField(FormField formField) {
        if (this.formField != null)
            throw new IllegalArgumentException("Form field already initialized!");

        this.formField = formField;
        this.initialize();
    }

    protected TextView getLabel() {
        return ((TextView) findViewById(R.id.label));
    }

    protected TextView getHelpText() {
        return (TextView) findViewById(R.id.help_text);
    }

    protected void initialize() {
        getLabel().setText(formField.getDisplayName());
        getHelpText().setText(formField.getHelpText());
        this.setVisibility(formField.isEnabled() ? VISIBLE : GONE);
        this.setEnabled(formField.isEditable());
    }

}
