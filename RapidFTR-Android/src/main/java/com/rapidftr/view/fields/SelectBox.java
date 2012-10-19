package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rapidftr.R;

public class SelectBox extends BaseView {

    public SelectBox(Context context) {
        super(context);
    }

    public SelectBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected Spinner getSpinner() {
        return (Spinner) findViewById(R.id.field_options);
    }

    @Override
    protected void initialize() {
        super.initialize();
        ArrayAdapter<String> optionsAdapter =
            new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, formField.getOptionStrings());
        getSpinner().setAdapter(optionsAdapter);

    }
}
