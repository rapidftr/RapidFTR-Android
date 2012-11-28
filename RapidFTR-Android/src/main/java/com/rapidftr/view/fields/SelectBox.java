package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.common.base.Strings;
import com.rapidftr.R;
import org.json.JSONException;

import java.util.ArrayList;
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
    protected void initialize() throws JSONException {
        super.initialize();

        List<String> selectOptions = new ArrayList<String>( formField.getOptionStrings() );
        if (selectOptions.size() == 0 || !Strings.isNullOrEmpty(selectOptions.get(0))) {
            selectOptions.add(0, "");
        }

        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, selectOptions);
        getSpinner().setAdapter(optionsAdapter);

        if (child.has(formField.getId())) {
            try {
                String formFieldValue = child.getString(formField.getId());
                if (selectOptions.contains(formFieldValue)) {
                    getSpinner().setSelection(selectOptions.indexOf(formFieldValue));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    child.put(formField.getId(), getSpinner().getAdapter().getItem(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                try {
                    child.put(formField.getId(), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);    //To change body of overridden methods use File | Settings | File Templates.

        getSpinner().setEnabled(enabled);
        getSpinner().setClickable(enabled);
        getSpinner().setFocusable(enabled);
        getSpinner().setFocusableInTouchMode(enabled);
    }
}
