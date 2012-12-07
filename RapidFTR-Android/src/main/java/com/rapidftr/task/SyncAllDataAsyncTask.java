package com.rapidftr.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;
import com.rapidftr.RapidFtrApplication;

import java.io.IOException;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class SyncAllDataAsyncTask extends AsyncTask<Void, String, Boolean> {

    public static final int NOTIFICATION_ID = 1010;
    private FormService formService;
    private ChildService childService;
    private ChildRepository childRepository;
    private RapidFtrActivity context;
    private int MAX_PROGRESS = 30;
    private Notification notification;
    private NotificationManager notificationManager;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        this.formService = formService;
        this.childService = childService;
        this.childRepository = childRepository;
    }

    @Override
    protected void onPreExecute() {
        initNotifiers();
        configureNotification();
    }

    private void configureNotification() {
        Intent intent = new Intent(context, RapidFtrActivity.class);
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.progress_bar);
    }

    @Override
    protected Boolean doInBackground(Void... notRelevant) {
        try {
            setProgressAndNotify("Step 1 of 3 - Syncing Form Sections...", 0);
            formService.getPublishedFormSections();
            setProgressAndNotify("Step 2 of 3 - Sending records to server...", 10);
            List<Child> childrenToSyncWithServer = childRepository.toBeSynced();
            sendChildrenToServer(childrenToSyncWithServer);
            setProgressAndNotify("Step 3 of 3 - Bringing down records from server...", 20);
            saveIncomingChildren();
            setProgressAndNotify("Sync complete.", 30);
        } catch (Exception e) {
            Log.e("SyncAllDataTask", "Error in sync", e);
            publishProgress("Error in syncing. Try again after some time.");
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Toast.makeText(RapidFtrApplication.getApplicationInstance(), values[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void initNotifiers() {
        notification = new Notification(R.drawable.icon, "Syncing in progress...", currentTimeMillis());
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    private void setProgressAndNotify(String statusText, int progress) {
        notification.contentView.setTextViewText(R.id.status_text, statusText);
        notification.contentView.setProgressBar(R.id.status_progress, MAX_PROGRESS, progress, false);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void sendChildrenToServer(List<Child> childrenToSyncWithServer) throws IOException, JSONException {
        String subStatusFormat = "Records %s/" + childrenToSyncWithServer.size() + " Uploaded";
        int counter = 0;
        for (Child child : childrenToSyncWithServer) {
            childService.sync(child);
            setProgressAndNotify(String.format(subStatusFormat, ++counter), 15);
        }
    }

    private void saveIncomingChildren() throws IOException {
        for (Child incomingChild : childService.getAllChildren()) {
            try {
                incomingChild.setSynced(true);
                if (childRepository.exists(incomingChild.getUniqueId())) {
                    childRepository.update(incomingChild);
                } else {
                    childRepository.createOrUpdate(incomingChild);
                }
                childService.setPhoto(incomingChild);
            } catch (Exception e) {
                Log.e("SyncAllDataTask", "Error syncing child", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void setContext(RapidFtrActivity context) {
        this.context = context;
    }
}
