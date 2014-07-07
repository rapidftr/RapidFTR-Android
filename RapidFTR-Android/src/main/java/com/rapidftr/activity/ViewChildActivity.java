package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildSyncService;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SyncSingleRecordTask;
import com.rapidftr.utils.http.FluentRequest;
import org.json.JSONException;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;


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
        SyncSingleRecordTask task = createChildSyncTask();
        task.setActivity(this);
        RapidFtrApplication.getApplicationInstance()
                .setAsyncTaskWithDialog((AsyncTaskWithDialog) AsyncTaskWithDialog.wrap(this, task, R.string.sync_progress, R.string.sync_success, R.string.sync_failure).execute(child));
    }

    protected SyncSingleRecordTask createChildSyncTask() {
        ChildRepository childRepository = inject(ChildRepository.class);
        return new SyncSingleRecordTask(new ChildSyncService(this.getContext(), childRepository, new FluentRequest()),
                childRepository, getCurrentUser()) {
            @Override
            public Boolean doInBackground(BaseModel... params) {
                try {
                    Child childRecord = (Child) service.sync(params[0], currentUser);
                    if (!childRecord.isSynced()) {
                        RapidFtrApplication.getApplicationInstance()
                                .getAsyncTaskWithDialog().setFailureMessage(childRecord.getSyncLog());
                    }
                    return childRecord.isSynced();
                } catch (Exception e) {
                    Log.e(APP_IDENTIFIER, "Error syncing one child record", e);
                    return false;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_single_menu, menu);
        try {
            if (!child.isSynced() && child.getSyncLog() != null) {
                menu.findItem(R.id.synchronize_log).setVisible(true);
            }
            if (!getCurrentUser().isVerified()) {
                menu.findItem(R.id.sync_single).setVisible(false);
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
            case R.id.sync_single:
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
