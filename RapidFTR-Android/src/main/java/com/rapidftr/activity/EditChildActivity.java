package com.rapidftr.activity;

import android.os.Bundle;
import android.view.Menu;
import com.rapidftr.R;
import org.json.JSONException;

public class EditChildActivity extends RegisterChildActivity {

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        load();
        super.initializeData(savedInstanceState);
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.save);
        setTitle(child.getShortId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu, menu);
        return true;
    }

}
