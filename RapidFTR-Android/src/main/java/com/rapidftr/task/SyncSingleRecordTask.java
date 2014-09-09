package com.rapidftr.task;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.SyncService;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

public class SyncSingleRecordTask extends AsyncTaskWithDialog<BaseModel, Void, Boolean> {

    protected SyncService service;
    protected Repository repository;
    protected User currentUser;
    private Activity activity;

    @Inject
    public SyncSingleRecordTask(SyncService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @Override
    public Boolean doInBackground(BaseModel... params) {
        try {
            service.sync(params[0], currentUser);
            return true;
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Error syncing one child record", e);
            ((RapidFtrApplication) activity.getApplication()).showNotification(service.getNotificationId(), service.getNotificationTitle(), e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void cancel() {
        this.cancel(false);
    }
}