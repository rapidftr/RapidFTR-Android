package com.rapidftr.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;
import com.rapidftr.task.AsyncTaskWithDialog;
import lombok.RequiredArgsConstructor;
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

    protected void sync() {
        SyncChildTask task = new SyncChildTask(inject(ChildService.class));
        AsyncTaskWithDialog.wrap(this, task, R.string.sync_progress, R.string.sync_success, R.string.sync_failure).execute(child);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.synchronize_child:
                sync();
                return true;
        }

        return false;
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    protected class SyncChildTask extends AsyncTaskWithDialog<Child, Void, Child> {

        protected final ChildService service;

        @Override
        protected Child doInBackground(Child... children) {
            try {
                return service.sync(child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Child child) {
            restart();
        }
    }

}
