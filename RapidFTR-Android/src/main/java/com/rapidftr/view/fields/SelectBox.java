package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rapidftr.R;
import org.json.JSONException;

import java.util.List;

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
        List<String> selectOptions = formField.getOptionStrings();
        ArrayAdapter<String> optionsAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, selectOptions);
        getSpinner().setAdapter(optionsAdapter);

        if (child.has(formField.getId())) {
            try {
                String formFieldValue = child.getString(formField.getId());
                if (selectOptions.contains(formFieldValue)) {
                    getSpinner().setSelection(selectOptions.indexOf(formFieldValue));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    child.put(formField.getId(), getSpinner().getAdapter().getItem(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                try {
                    child.put(formField.getId(), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
