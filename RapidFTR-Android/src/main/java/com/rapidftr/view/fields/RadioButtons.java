package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.rapidftr.R;
import org.json.JSONException;

public class RadioButtons extends BaseView {

    public RadioButtons(Context context) {
        super(context);
    }

    public RadioButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected RadioGroup getRadioGroup() {
        return (RadioGroup) findViewById(R.id.values);
    }

    @Override
    protected void initialize() {
        try {
            super.initialize();
            for (String options : formField.getLocalizedOptionStrings())
                getRadioGroup().addView(createRadioButton(options));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected RadioButton createRadioButton(String optionName) throws JSONException {
        super.initialize();
        final RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.form_radio_option, null);
        radioButton.setText(optionName);
        radioButton.setTag(optionName);

        if (child.has(formField.getId()) && child.getString(formField.getId()).equals(optionName)) {
            radioButton.setChecked(true);
        }

        radioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                child.put(formField.getId(), radioButton.getText());
                for (int i = 0; i < getRadioGroup().getChildCount(); i++) {
                    RadioButton button = (RadioButton) getRadioGroup().getChildAt(i);
                    if (!button.getText().equals(radioButton.getText())) {
                        button.setChecked(false);
                    }
                }
            }

        });
        return radioButton;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        RadioGroup group = getRadioGroup();
        for (int i=0, j=group.getChildCount(); i<j; i++) {
            View view = group.getChildAt(i);
            view.setEnabled(enabled);
            view.setClickable(enabled);
        }
    }
}
