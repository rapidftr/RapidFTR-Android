package com.rapidftr.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.FormService;
import com.rapidftr.service.SyncService;
import lombok.Setter;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public abstract class SynchronisationAsyncTask<T extends BaseModel> extends AsyncTask<Object, String, Boolean> {

    public static int NOTIFICATION_ID = 1010 + new Random().nextInt(10);
    private static final String SYNC_ALL = "SYNC_ALL";
    private static final String CANCEL_SYNC_ALL = "CANCEL_SYNC_ALL";

    protected FormService formService;
    protected SyncService<T> recordSyncService;
    protected Repository<T> repository;
    protected User currentUser;
    protected RapidFtrActivity context;
    protected Notification notification;
    protected NotificationManager notificationManager;

    protected int formSectionProgress;
    protected int maxProgress;
    @Setter
    private String successMessage = "Records Successfully Synchronized";

    public SynchronisationAsyncTask(FormService formService, SyncService<T> recordSyncService, Repository<T> repository, User user) {
        this.formService = formService;
        this.recordSyncService = recordSyncService;
        this.repository = repository;
        currentUser = user;
    }

    @Override
    protected void onPreExecute() {
        RapidFtrApplication.getApplicationInstance().setSyncTask(this);
        toggleMenu(CANCEL_SYNC_ALL);
    }

    @Override
    protected Boolean doInBackground(Object... notRelevant) {
        try {
            sync();
            return true;
        } catch (HttpException e) {
            Log.e("SyncAllDataTask", "HTTPError in sync", e);

            String message = RapidFtrApplication.getApplicationInstance().getString(R.string.session_timeout);
            if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
                message = e.getMessage();
            }

            publishProgress(message);
            return false;
        } catch (Exception e) {
            Log.e("SyncAllDataTask", "Error in sync", e);
            publishProgress(context.getString(R.string.sync_error));
            return false;
        }
    }

    protected abstract void sync() throws JSONException, IOException, HttpException;

    protected void getFormSections() throws IOException {
        if (!isCancelled()) {
            formService.downloadPublishedFormSections();
        }
    }

    void setProgressBarParameters(List<String> idsToDownload, List<?> recordsToSyncWithServer) {
        int totalRecordsToSynchronize = idsToDownload.size() + recordsToSyncWithServer.size();
        formSectionProgress = totalRecordsToSynchronize / 4 == 0 ? 20 : totalRecordsToSynchronize / 4;
        maxProgress = totalRecordsToSynchronize + formSectionProgress;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        RapidFtrApplication.getApplicationInstance().showNotification(recordSyncService.getNotificationId(),
                recordSyncService.getNotificationTitle(), values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        toggleMenu(SYNC_ALL);
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
        if (result) {
            RapidFtrApplication.getApplicationInstance().showNotification(recordSyncService.getNotificationId(),
                    recordSyncService.getNotificationTitle(),
                    successMessage);
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), successMessage, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(),
                    RapidFtrApplication.getApplicationInstance().getString(R.string.sync_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        toggleMenu(SYNC_ALL);
        RapidFtrApplication.getApplicationInstance().cancelNotification(recordSyncService.getNotificationId());
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
    }

    private void toggleMenu(String showMenu) {
        context.getMenu().getItem(0).setVisible(showMenu.equals(SYNC_ALL));
        context.getMenu().getItem(1).setVisible(showMenu.equals(CANCEL_SYNC_ALL));
    }

    protected void setProgressAndNotify(String statusText, int progress) {
        if (!isCancelled()) {
            RapidFtrApplication.getApplicationInstance().showProgressNotification(recordSyncService.getNotificationId(),
                    context.getString(R.string.sync_title), statusText, maxProgress, progress, false);
        }
    }

    void sendRecordsToServer(List<T> recordsToSyncWithServer) throws IOException, JSONException, HttpException {
        setProgressAndNotify(context.getString(R.string.synchronize_step_2), formSectionProgress);
        String subStatusFormat = "Uploading Record %s of " + recordsToSyncWithServer.size();
        int counter = 0;
        int startProgress = formSectionProgress;
        for (T baseModel : recordsToSyncWithServer) {
            if (isCancelled()) {
                break;
            }
            recordSyncService.sync(baseModel, currentUser);
            setProgressAndNotify(String.format(subStatusFormat, ++counter), startProgress);
            startProgress += 1;
        }
    }

    protected void saveIncomingRecords(List<String> idsToDownload, int startProgress) throws IOException, JSONException, HttpException {
        String subStatusFormat = "Downloading Record %s of" + idsToDownload.size();
        int counter = 0;
        setProgressAndNotify(context.getString(R.string.synchronize_step_3), startProgress);

        for (String idToDownload : idsToDownload) {
            T incomingRecord = recordSyncService.getRecord(idToDownload);
            if (isCancelled()) {
                break;
            }
            try {
                repository.createOrUpdateWithoutHistory(incomingRecord);
                recordSyncService.setMedia(incomingRecord);
                setProgressAndNotify(String.format(subStatusFormat, ++counter), startProgress);
                startProgress += 1;
            } catch (Exception e) {
                Log.e("SyncAllDataTask", "Error syncing record", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void setContext(RapidFtrActivity context) {
        this.context = context;
    }
}
