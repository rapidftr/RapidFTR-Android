package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import com.rapidftr.R;

public class TextField extends BaseView {

    public TextField(Context context) {
        super(context);
    }

    public TextField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected EditText getEditTextView() {
        return (EditText) findViewById(R.id.value);
    }

    protected String getText() {
        CharSequence text = getEditTextView().getText();
        return text == null ? null : text.toString();
    }

    protected void setText(Object newText) {
        getEditTextView().setText(newText == null ? "" : newText.toString());
    }

    @Override
    protected void initialize() {
        super.initialize();
        setText(formField.getValue());
    }

}
