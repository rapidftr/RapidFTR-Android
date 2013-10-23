package com.rapidftr.task;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.SyncService;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

public class SyncRecordTask extends AsyncTaskWithDialog<BaseModel, Void, Boolean> {

    protected final SyncService service;
    protected final Repository repository;
    protected final User currentUser;
    private Activity activity;

    @Inject
    public SyncRecordTask(ChildService service, ChildRepository repository, User currentUser) {
        this.service = service;
        this.repository = repository;
        this.currentUser = currentUser;
    }

    @Override
    public Boolean doInBackground(BaseModel... params) {
        try {
            service.sync(params[0], currentUser);
            return true;
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Error syncing one child record", e);
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