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

public class SyncUnverifiedUsersDataAsyncTask extends SynchronisationAsyncTask {

    @Inject
    public SyncUnverifiedUsersDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        super(formService, childService, childRepository);
    }

    protected void sync() throws JSONException, IOException {
        List<Child> childrenToSyncWithServer = childRepository.currentUsersUnsyncedRecords();
        setProgressBarParameters(Lists.<String>newArrayList(), childrenToSyncWithServer);

        getFormSections();
//        sendChildrenToServer(childrenToSyncWithServer);
        setProgressAndNotify(context.getString(R.string.sync_complete), MAX_PROGRESS);
    }


}
