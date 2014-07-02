package com.rapidftr.task;

import android.widget.Toast;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication_;
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
        List<T> recordsToUpload = repository.toBeSynced();
        List<String> idsToDownload;
        Boolean isBlacklisted = deviceService.isBlacklisted();
        if(isBlacklisted){
            sendRecordsToServer(recordsToUpload);
            if (repository.toBeSynced().isEmpty())
            {
                deviceService.wipeData();
            }
        } else {
            idsToDownload = recordSyncService.getIdsToDownload();
            setProgressBarParameters(idsToDownload, recordsToUpload);
            setProgressAndNotify(context.getString(R.string.synchronize_step_1), 0);

            sendRecordsToServer(recordsToUpload);
            downloadRecordsFromServer(idsToDownload, numberOfUploadedRecords(recordsToUpload));
        }
    }

    private int numberOfUploadedRecords(List<T> recordsToUpload) throws JSONException {
        return formSectionProgress + recordsToUpload.size();
    }

    private void downloadRecordsFromServer(List<String> idsToDownload, int startProgressForDownloadingRecords)
            throws IOException, JSONException, HttpException {
        getFormSections();
        saveIncomingRecords(idsToDownload, startProgressForDownloadingRecords);
        setProgressAndNotify(context.getString(R.string.sync_complete), maxProgress);
    }

}
