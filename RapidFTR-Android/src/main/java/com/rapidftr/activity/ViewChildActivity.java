package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import com.rapidftr.R;
import org.json.JSONException;

public class ViewChildActivity extends BaseChildActivity {

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_register_child);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    edit();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(child.getShortId());
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
    }

}
