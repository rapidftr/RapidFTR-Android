package com.rapidftr.view.fields;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import com.rapidftr.R;
import org.json.JSONException;

public class TextField extends BaseView {

    public TextField(Context context) {
        super(context);
    }

    public TextField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditText getEditTextView() {
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
    protected void initialize() throws JSONException {
        super.initialize();
        setText(formField.getValue());

        if (model.has(formField.getId())) {
            try {
                getEditTextView().setText(model.get(formField.getId()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        getEditTextView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                model.put(formField.getId(), s.toString());
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        getEditTextView().setEnabled(enabled);
        getEditTextView().setClickable(enabled);
        getEditTextView().setFocusable(enabled);
        getEditTextView().setFocusableInTouchMode(enabled);
        getEditTextView().setCursorVisible(enabled);
    }
}
