package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.rapidftr.R;

public class RadioButtons extends BaseView {

    public RadioButtons(Context context) {
        super(context);
    }

    public RadioButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected RadioGroup getRadioGroup(){
        return (RadioGroup) findViewById(R.id.values);
    }

    protected RadioButton createRadioButton(String option){
        super.initialize();
        RadioButton radioButton = (RadioButton)LayoutInflater.from(getContext()).inflate(R.layout.form_radio_option, null);
        radioButton.setText(option);
        return radioButton;
    }

    @Override
    protected void initialize() {
        for(String options : formField.getOptionStrings())
            getRadioGroup().addView(createRadioButton(options));
    }

}
