package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.rapidftr.R;

public class TextField extends BaseView {

    public static final int LAYOUT_RESOURCE_ID = R.layout.form_text_field;

    public TextField(Context context) {
        super(context);
    }

    public TextField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected TextView getTextView() {
        return (TextView) findViewById(R.id.value);
    }

    @Override
    protected void initialize() {
        getTextView().setText(formField.getValue() != null ? formField.getValue().toString() : "");
    }

}
