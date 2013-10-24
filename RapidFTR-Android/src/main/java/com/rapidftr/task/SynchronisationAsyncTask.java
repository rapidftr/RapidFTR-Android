package com.rapidftr.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.FormService;
import com.rapidftr.service.SyncService;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public abstract class SynchronisationAsyncTask<T extends BaseModel> extends AsyncTask<Object, String, Boolean> {

    public static final int NOTIFICATION_ID = 1010;
    private static final String SYNC_ALL = "SYNC_ALL";
    private static final String CANCEL_SYNC_ALL = "CANCEL_SYNC_ALL";

    protected FormService formService;
    protected SyncService<T> recordService;
    protected Repository<T> repository;
    protected User currentUser;
    protected RapidFtrActivity context;
    protected Notification notification;
    protected NotificationManager notificationManager;

    protected int formSectionProgress;
    protected int maxProgress;

    public SynchronisationAsyncTask(FormService formService, SyncService<T> recordService, Repository<T> repository, User user) {
        this.formService = formService;
        this.recordService = recordService;
        this.repository = repository;
        currentUser = user;
    }

    @Override
    protected void onPreExecute() {
        RapidFtrApplication.getApplicationInstance().setSyncTask(this);
        toggleMenu(CANCEL_SYNC_ALL);
        initNotifiers();
        configureNotification();
    }

    @Override
    protected Boolean doInBackground(Object... notRelevant) {
        try {
            sync();
            return true;
        } catch (HttpException e) {
	        notificationManager.cancel(NOTIFICATION_ID);
	        Log.e("SyncAllDataTask", "Error in sync", e);
	        publishProgress(context.getString(R.string.session_timeout));
	        return false;
        } catch (Exception e) {
            notificationManager.cancel(NOTIFICATION_ID);
            Log.e("SyncAllDataTask", "Error in sync", e);
            publishProgress(context.getString(R.string.sync_error));
            return false;
        }
    }

    protected abstract void sync() throws JSONException, IOException, HttpException;


    private void configureNotification() {
        Intent intent = new Intent(context, RapidFtrActivity.class);
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.progress_bar);
    }

    protected void getFormSections() throws IOException {
        if (!isCancelled()) {
            formService.getPublishedFormSections();
        }
    }

    void setProgressBarParameters(List<String> idsToDownload, List<?> recordsToSyncWithServer) {
        int totalRecordsToSynchronize = idsToDownload.size() + recordsToSyncWithServer.size();
        formSectionProgress = totalRecordsToSynchronize/4 == 0 ? 20 : totalRecordsToSynchronize/4;
        maxProgress = totalRecordsToSynchronize + formSectionProgress;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Toast.makeText(RapidFtrApplication.getApplicationInstance(), values[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        toggleMenu(SYNC_ALL);
        notificationManager.cancel(NOTIFICATION_ID);
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
    }

    @Override
    protected void onCancelled() {
        toggleMenu(SYNC_ALL);
        notificationManager.cancel(NOTIFICATION_ID);
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
    }

    private void toggleMenu(String showMenu) {
        context.getMenu().getItem(0).setVisible(showMenu.equals(SYNC_ALL));
        context.getMenu().getItem(1).setVisible(showMenu.equals(CANCEL_SYNC_ALL));
    }

    private void initNotifiers() {
        notification = new Notification(R.drawable.icon, context.getString(R.string.sync_progress), currentTimeMillis());
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected void setProgressAndNotify(String statusText, int progress) {
        if (!isCancelled()) {
            notification.contentView.setTextViewText(R.id.status_text, statusText);
            notification.contentView.setProgressBar(R.id.status_progress, maxProgress, progress, false);
            notificationManager.notify(NOTIFICATION_ID, notification);
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
            recordService.sync(baseModel, currentUser);
            setProgressAndNotify(String.format(subStatusFormat, ++counter), startProgress);
            startProgress += 1;
        }
    }

    protected void saveIncomingRecords(List<String> idsToDownload, int startProgress) throws IOException, JSONException, HttpException {
        String subStatusFormat = "Downloading Record %s of" + idsToDownload.size();
        int counter = 0;
        setProgressAndNotify(context.getString(R.string.synchronize_step_3), startProgress);

        for (String idToDownload : idsToDownload) {
            T incomingRecord = recordService.getRecord(idToDownload);
            if (isCancelled()) {
                break;
            }
            try {
                if (repository.exists(incomingRecord.getUniqueId())) {
                    repository.update(incomingRecord);
                } else {
                    repository.createOrUpdate(incomingRecord);
                }
                recordService.setMedia(incomingRecord);
                setProgressAndNotify(String.format(subStatusFormat, ++counter), startProgress);
                startProgress += 1 ;
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
