package com.rapidftr.task;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.DeviceService;
import com.rapidftr.service.FormService;
import com.rapidftr.utils.DeviceAdmin;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SyncAllDataAsyncTask extends SynchronisationAsyncTask {

    private RapidFtrApplication application;
    private DeviceService deviceService;
    private DeviceAdmin deviceAdmin;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService,
                                DeviceService deviceService, ChildRepository childRepository,
                                User user, RapidFtrApplication application, DeviceAdmin deviceAdmin) {
        super(formService, childService, childRepository, user);
        this.application = application;
        this.deviceService = deviceService;
        this.deviceAdmin = deviceAdmin;
    }

    protected void sync() throws JSONException, IOException, HttpException {

        ArrayList<String> idsToDownload = new ArrayList<String>();
        Boolean blacklisted = deviceService.isBlacklisted();

        if(blacklisted){
            uploadChildrenToSyncWithServer(idsToDownload);
            deviceAdmin.wipeData();
        } else {
            idsToDownload = getAllIdsForDownload();
            int startProgressForDownloadingChildren = uploadChildrenToSyncWithServer(idsToDownload);
            downloadChildrenFromServerToSync(idsToDownload, startProgressForDownloadingChildren);
        }
    }

    private int uploadChildrenToSyncWithServer( ArrayList<String> idsToDownload) throws JSONException, IOException {
        List<Child> childrenToSyncWithServer = childRepository.toBeSynced();
        setProgressBarParameters(idsToDownload, childrenToSyncWithServer);
        setProgressAndNotify(context.getString(R.string.synchronize_step_1), 0);
        getFormSections();
        sendChildrenToServer(childrenToSyncWithServer);

        return formSectionProgress + childrenToSyncWithServer.size();
    }

    private void downloadChildrenFromServerToSync(ArrayList<String> idsToDownload,
                                                  int startProgressForDownloadingChildren)
            throws IOException, JSONException {
        saveIncomingChildren(idsToDownload, startProgressForDownloadingChildren);
        setProgressAndNotify(context.getString(R.string.sync_complete), maxProgress);
    }

}
