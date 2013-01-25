package com.rapidftr.task;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class SyncUnverifiedDataAsyncTask extends SynchronisationAsyncTask {

    @Inject
    public SyncUnverifiedDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        super(formService, childService, childRepository);
    }

    protected void sync() throws JSONException, IOException {
        List<Child> childrenToSyncWithServer = childRepository.currentUsersUnsyncedRecords();
        getFormSections();
        for (Child child : childrenToSyncWithServer) {
            childService.syncUnverified(child);
            child.setSynced(true);
            childRepository.update(child);
        }
        setProgressAndNotify(context.getString(R.string.sync_complete), MAX_PROGRESS);
    }
}
