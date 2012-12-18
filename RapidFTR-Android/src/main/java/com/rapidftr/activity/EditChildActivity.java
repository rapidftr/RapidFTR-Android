package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import com.rapidftr.R;
import com.rapidftr.task.AsyncTaskWithDialog;
import org.json.JSONException;

public class EditChildActivity extends BaseChildActivity {

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        load();
        super.initializeData(savedInstanceState);
    }

    protected void initializeView() {
        setContentView(R.layout.activity_register_child);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              saveChild();
            }
        });
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.save);
        setTitle(child.getShortId());
    }

    @Override
    protected void saveChild() {
        AsyncTaskWithDialog.wrap(EditChildActivity.this, new SaveChildTask(), R.string.save_child_progress, R.string.save_child_success, R.string.save_child_invalid).execute();
    }

}
