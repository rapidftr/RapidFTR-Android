package com.rapidftr.task;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

public class SyncChildTask extends AsyncTaskWithDialog<Child, Void, Boolean> {

    protected final ChildService service;
    protected final ChildRepository repository;
    protected final User currentUser;
    private Activity activity;

    @Inject
    public SyncChildTask(ChildService service, ChildRepository repository, User currentUser) {
        this.service = service;
        this.repository = repository;
        this.currentUser = currentUser;
    }

    @Override
    public Boolean doInBackground(Child... children) {
        try {
            service.sync(children[0], currentUser);
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