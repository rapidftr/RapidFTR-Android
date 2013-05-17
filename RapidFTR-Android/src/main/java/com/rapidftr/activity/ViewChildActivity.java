package com.rapidftr.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SyncChildTask;
import org.json.JSONException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;


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
        if (!this.getContext().isOnline()) {
            makeToast(R.string.connection_off);
        } else {
            SyncChildTask task = inject(SyncChildTask.class);
            task.setActivity(this);
            RapidFtrApplication.getApplicationInstance().setAsyncTaskWithDialog((AsyncTaskWithDialog) AsyncTaskWithDialog.wrap(this, task, R.string.sync_progress, R.string.sync_success, R.string.sync_error).execute(child));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu, menu);
        try {
            if (!child.isSynced() && child.getSyncLog() != null) {
                menu.findItem(R.id.synchronize_log).setVisible(true);
            }
            if (!getCurrentUser().isVerified()) {
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
                if (isNullOrEmpty(getCurrentUser().getServerUrl())) {
                    getServerAndSync();
                } else {
                    sync();
                }
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

    public void getServerAndSync() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter sync location");
        alert.setMessage("Please enter the location you wish to synchronise with");
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getContext().getSharedPreferences().edit().putString(SERVER_URL_PREF, input.getText().toString()).commit();
                getCurrentUser().setServerUrl(input.getText().toString());
                sync();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
                startActivity(new Intent(getContext(), ViewAllChildrenActivity.class));
            }
        });
        alert.create().show();
    }

    protected void showSyncLog() {
        Toast.makeText(this, getText(R.string.temp_sync_error), Toast.LENGTH_LONG).show();
    }

}
