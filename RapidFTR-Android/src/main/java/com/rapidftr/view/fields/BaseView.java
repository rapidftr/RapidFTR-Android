package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.forms.FormField;
import com.rapidftr.model.Child;
import org.json.JSONException;

public abstract class BaseView extends LinearLayout {

    protected FormField formField;

    protected Child child;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize(FormField formField, Child child) {
        if (this.formField != null)
            throw new IllegalArgumentException("Form field already initialized!");

        this.formField = formField;
        this.child = child;
        this.setTag(formField.getId());
        this.setId(formField.getId().hashCode());

        try {
            this.initialize();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected TextView getLabel() {
        return ((TextView) findViewById(R.id.label));
    }

    protected TextView getHelpText() {
        return (TextView) findViewById(R.id.help_text);
    }

    protected void initialize() throws JSONException {
        getLabel().setText(formField.getLocalizedDisplayName());
        getHelpText().setText(formField.getLocalizedHelpText());
    }

}
