package com.rapidftr.task;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SyncAllDataAsyncTask extends SynchronisationAsyncTask {

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        super(formService, childService, childRepository);
    }

    protected void sync() throws JSONException, IOException {
        ArrayList<String> idsToDownload = getAllIdsForDownload();
        List<Child> childrenToSyncWithServer = childRepository.toBeSynced();
        setProgressBarParameters(idsToDownload, childrenToSyncWithServer);

        getFormSections();
        sendChildrenToServer(childrenToSyncWithServer);
        int startProgressForDownloadingChildren = FORM_SECTION_PROGRESS + childrenToSyncWithServer.size();
        saveIncomingChildren(idsToDownload, startProgressForDownloadingChildren);

        setProgressAndNotify(context.getString(R.string.sync_complete), MAX_PROGRESS);
    }

}
