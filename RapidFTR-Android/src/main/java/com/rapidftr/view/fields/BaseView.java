package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.forms.FormField;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import org.json.JSONException;

public abstract class BaseView extends LinearLayout {

    protected FormField formField;

    protected BaseModel model;

    @Inject
    public BaseView(Context context) {
        super(context);

    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void initialize(FormField formField, BaseModel model) {
        if (this.formField != null)
            throw new IllegalArgumentException("Form field already initialized!");

        this.formField = formField;
        this.model = model;
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
        if (formField.getId().equals("current_photo_key"))
            getLabel().setText("");
        else
            getLabel().setText(formField.getLocalizedDisplayName());
        getHelpText().setText(formField.getLocalizedHelpText());
    }

}
