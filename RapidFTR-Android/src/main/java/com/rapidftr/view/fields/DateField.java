package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;
import com.rapidftr.R;

public class DateField extends BaseView {

    public DateField(Context context) {
        super(context);
    }

    public DateField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected DatePicker getDatePickerView() {
        return (DatePicker) findViewById(R.id.value);
    }

}
