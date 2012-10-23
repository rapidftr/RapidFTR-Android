package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
        JSONArray options;
        if (child.has(formField.getId()) && child.get(formField.getId()) instanceof JSONArray) {
            options = (JSONArray) child.get(formField.getId());
            String[] tempOptions = new String[options.length()];
            for (int i = 0; i < tempOptions.length; i++) {
                if (options.getString(i).equals(optionName)){
                    checkBox.setChecked(true);
                }
            }
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONArray options = new JSONArray();
                    if (child.has(formField.getId()) && child.get(formField.getId()) instanceof JSONArray) {
                        options = (JSONArray) child.get(formField.getId());
                    }

                    if (isChecked) {
                        options.put(buttonView.getText().toString());
                        child.put(formField.getId(), options);
                    } else {
                        String[] tempOptions = new String[options.length()];
                        for (int i = 0; i < tempOptions.length; i++) {
                            if (!options.getString(i).equals(buttonView.getText().toString()))
                                tempOptions[i] = options.getString(i);
                        }
                        child.put(formField.getId(), tempOptions);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return checkBox;
    }

}
