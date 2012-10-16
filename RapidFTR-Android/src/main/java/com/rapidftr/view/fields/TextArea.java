package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import com.rapidftr.R;

public class TextArea extends BaseView {

    public static final int LAYOUT_RESOURCE_ID = R.layout.form_textarea;

    public TextArea(Context context) {
        super(context);
    }

    public TextArea(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected EditText getEditTextView() {
        return (EditText) findViewById(R.id.value);
    }

}
