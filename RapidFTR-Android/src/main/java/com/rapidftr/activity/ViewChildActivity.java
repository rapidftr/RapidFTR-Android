package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SyncChildTask;
import org.json.JSONException;


public class ViewChildActivity extends BaseChildActivity {

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_view_child);
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
        SyncChildTask task = inject(SyncChildTask.class);
        task.setActivity(this);
        RapidFtrApplication.getApplicationInstance().setAsyncTaskWithDialog((AsyncTaskWithDialog) AsyncTaskWithDialog.wrap(this, task, R.string.sync_progress, R.string.sync_success, R.string.sync_failure).execute(child));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu, menu);
        try {
            if (!child.isSynced() && child.getSyncLog() != null) {
                menu.findItem(R.id.synchronize_log).setVisible(true);
            }
            if (!getCurrentUser().isVerified()) {
                menu.findItem(R.id.synchronize_child).setVisible(false);
                menu.getItem(4).setVisible(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                return true;
            case R.id.synchronize_child:
                sync();
                return true;
            case R.id.synchronize_log:
                showSyncLog();
                return true;
            case R.id.logout:
                inject(LogOutService.class).attemptLogOut(this);
                return true;
            case R.id.info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
        }
        return false;
    }

    protected void showSyncLog() {
        Toast.makeText(this, getText(R.string.temp_sync_error), Toast.LENGTH_LONG).show();
    }

}
