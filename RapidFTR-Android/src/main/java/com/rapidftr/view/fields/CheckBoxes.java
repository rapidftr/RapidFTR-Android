package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.rapidftr.R;

public class CheckBoxes extends BaseView {

    public CheckBoxes(Context context) {
        super(context);
    }

    public CheckBoxes(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected LinearLayout getCheckBoxGroup() {
        return (LinearLayout) findViewById(R.id.values);
    }

    @Override
    protected void initialize() {
        getLabel().setText(formField.getDisplayName());
        for (String option : formField.getOptionStrings())
            getCheckBoxGroup().addView(createCheckBoxFor(option));
    }

    protected CheckBox createCheckBoxFor(String optionName) {
        CheckBox checkBox = (CheckBox) LayoutInflater.from(getContext()).inflate(R.layout.form_check_box, null);
        checkBox.setText(optionName);
        return checkBox;
    }

}
