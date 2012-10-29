package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.rapidftr.R;
import org.json.JSONArray;
import org.json.JSONException;

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
        try {
            super.initialize();
            for (String option : formField.getOptionStrings())
                getCheckBoxGroup().addView(createCheckBoxFor(option));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected CheckBox createCheckBoxFor(String optionName) throws JSONException {
        CheckBox checkBox = (CheckBox) LayoutInflater.from(getContext()).inflate(R.layout.form_check_box, null);
        checkBox.setText(optionName);
        checkBox.setTag(optionName);

        if (child.has(formField.getId()) && child.get(formField.getId()) instanceof JSONArray) {
            JSONArray options = child.getJSONArray(formField.getId());
            for (int i = 0; i < options.length(); i++) {
                if (options.getString(i).equals(optionName)){
                    checkBox.setChecked(true);
                }
            }
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    String value = buttonView.getText().toString();

                    if (isChecked) {
                        child.addToJSONArray(formField.getId(), value);
                    } else {
                        child.removeFromJSONArray(formField.getId(), value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return checkBox;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        LinearLayout group = getCheckBoxGroup();
        for (int i=0, j=group.getChildCount(); i<j; i++) {
            View view = group.getChildAt(i);
            view.setEnabled(enabled);
            view.setClickable(enabled);
            view.setFocusable(enabled);
            view.setFocusableInTouchMode(enabled);
        }
    }
}
