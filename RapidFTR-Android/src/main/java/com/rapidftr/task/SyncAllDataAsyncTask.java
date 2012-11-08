package com.rapidftr.task;

import android.os.AsyncTask;
import com.google.inject.Inject;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;

import java.util.List;

public class SyncAllDataAsyncTask extends AsyncTask<Child, Void, Boolean> {

    private FormService formService;
    private ChildService childService;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService) {
        this.formService = formService;
        this.childService = childService;
    }

    @Override
    protected Boolean doInBackground(Child... childrenToSyncWithServer) {
        try {
            formService.getPublishedFormSections();
            List<Child> incomingChildren = childService.getAllChildren();

            for (Child child : childrenToSyncWithServer) {
                childService.post(child);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
