package com.rapidftr.task;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SyncAllDataAsyncTask extends SynchronisationAsyncTask {

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository, User user) {
        super(formService, childService, childRepository, user);
    }

    protected void sync() throws JSONException, IOException, HttpException {
        ArrayList<String> idsToDownload = getAllIdsForDownload();
        List<Child> childrenToSyncWithServer = childRepository.toBeSynced();
        setProgressBarParameters(idsToDownload, childrenToSyncWithServer);

        setProgressAndNotify(context.getString(R.string.synchronize_step_1), 0);
        getFormSections();
        sendChildrenToServer(childrenToSyncWithServer);
        int startProgressForDownloadingChildren = formSectionProgress + childrenToSyncWithServer.size();
        saveIncomingChildren(idsToDownload, startProgressForDownloadingChildren);

        setProgressAndNotify(context.getString(R.string.sync_complete), maxProgress);

    }

}
