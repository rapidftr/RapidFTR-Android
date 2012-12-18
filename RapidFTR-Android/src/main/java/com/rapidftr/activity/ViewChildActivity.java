package com.rapidftr.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.LogOutService;
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
    protected void saveChild() {
        //Nothing to implement
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
    }

    protected void sync() {
        SyncChildTask task = new SyncChildTask(inject(ChildService.class),inject(ChildRepository.class));
        AsyncTaskWithDialog.wrap(this, task, R.string.sync_progress, R.string.sync_success, R.string.sync_failure).execute(child);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu, menu);
        try {
            if (!child.isSynced() && child.getSyncLog() != null) {
                menu.findItem(R.id.synchronize_log).setVisible(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.synchronize_child:
                sync();
                return true;
            case R.id.synchronize_log:
                showSyncLog();
                return true;
            case R.id.logout:
                inject(LogOutService.class).attemptLogOut(this);
                return true;
        }
        return false;
    }

    protected void showSyncLog() {
        try {
            Toast.makeText(this,child.getSyncLog(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected SyncChildTask getSyncChildTask(ChildService service, ChildRepository repository) {
        return new SyncChildTask(service, repository);
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    protected class SyncChildTask extends AsyncTaskWithDialog<Child, Void, Child> {

        protected final ChildService service;
        protected final ChildRepository repository;

        @Override
        protected Child doInBackground(Child... children) {
            try {
                return service.sync(child);
            } catch (Exception e) {
                try {
                    child.setSyncLog(e.getMessage());
                    repository.update(child);
                } catch (JSONException jsonException) {
                    logError(jsonException.getMessage());
                }
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Child child) {
            restart();
        }
    }

}
