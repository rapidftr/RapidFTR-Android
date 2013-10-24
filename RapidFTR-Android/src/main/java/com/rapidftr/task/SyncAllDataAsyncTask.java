package com.rapidftr.task;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.DeviceService;
import com.rapidftr.service.FormService;
import com.rapidftr.service.SyncService;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SyncAllDataAsyncTask<T extends BaseModel> extends SynchronisationAsyncTask<T> {

    private DeviceService deviceService;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, SyncService<T> recordService,
                                DeviceService deviceService, Repository<T> recordRepository,
                                User user) {
        super(formService, recordService, recordRepository, user);
        this.deviceService = deviceService;
    }

    protected void sync() throws JSONException, IOException, HttpException {

        List<String> idsToDownload = new ArrayList<String>();
        Boolean blacklisted = deviceService.isBlacklisted();
        if(blacklisted){
            uploadRecordsToServer(idsToDownload);
            if (repository.toBeSynced().isEmpty())
            {
                deviceService.wipeData();
            }
        } else {
            idsToDownload = recordSyncService.getIdsToDownload();
            int startProgressForDownloadingChildren = uploadRecordsToServer(idsToDownload);
            downloadRecordsFromServer(idsToDownload, startProgressForDownloadingChildren);
        }
    }

    private int uploadRecordsToServer(List<String> idsToDownload) throws JSONException, IOException, HttpException {
        List<T> recordsToSyncWithServer = repository.toBeSynced();
        setProgressBarParameters(idsToDownload, recordsToSyncWithServer);
        setProgressAndNotify(context.getString(R.string.synchronize_step_1), 0);
        getFormSections();
        sendRecordsToServer(recordsToSyncWithServer);

        return formSectionProgress + recordsToSyncWithServer.size();
    }

    private void downloadRecordsFromServer(List<String> idsToDownload, int startProgressForDownloadingRecords)
            throws IOException, JSONException, HttpException {
        saveIncomingRecords(idsToDownload, startProgressForDownloadingRecords);
        setProgressAndNotify(context.getString(R.string.sync_complete), maxProgress);
    }

}
