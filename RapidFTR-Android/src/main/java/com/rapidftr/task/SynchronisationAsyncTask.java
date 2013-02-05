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
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public abstract class SynchronisationAsyncTask extends AsyncTask<Void, String, Boolean> {

    public static final int NOTIFICATION_ID = 1010;
    private static final String SYNC_ALL = "SYNC_ALL";
    private static final String CANCEL_SYNC_ALL = "CANCEL_SYNC_ALL";

    protected FormService formService;
    protected ChildService childService;
    protected ChildRepository childRepository;
    protected User currentUser;
    protected RapidFtrActivity context;
    protected Notification notification;
    protected NotificationManager notificationManager;

    protected int formSectionProgress;
    protected int maxProgress;

    public SynchronisationAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository, User user) {
        this.formService = formService;
        this.childService = childService;
        this.childRepository = childRepository;
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
    protected Boolean doInBackground(Void... notRelevant) {
        try {
            sync();
            return true;
        } catch (Exception e) {
            notificationManager.cancel(NOTIFICATION_ID);
            Log.e("SyncAllDataTask", "Error in sync", e);
            publishProgress(context.getString(R.string.sync_error));
            return false;
        }
    }

    protected abstract void sync() throws JSONException, IOException;


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

    void setProgressBarParameters(ArrayList<String> idsToDownload, List<Child> childrenToSyncWithServer) {
        int totalRecordsToSynchronize = idsToDownload.size() + childrenToSyncWithServer.size();
        formSectionProgress = totalRecordsToSynchronize/4 == 0 ? 20 : totalRecordsToSynchronize/4;
        maxProgress = totalRecordsToSynchronize + formSectionProgress;
    }

    public ArrayList<String> getAllIdsForDownload() throws IOException, JSONException {
        HashMap<String,String> serverIdsRevs = childService.getAllIdsAndRevs();
        HashMap<String, String> repoIdsAndRevs = childRepository.getAllIdsAndRevs();
        ArrayList<String> idsToDownload = new ArrayList<String>();
        for(Map.Entry<String,String> serverIdRev : serverIdsRevs.entrySet()){
            if(!isServerIdExistingInRepository(repoIdsAndRevs, serverIdRev) || (repoIdsAndRevs.get(serverIdRev.getKey()) != null && isRevisionMismatch(repoIdsAndRevs, serverIdRev))){
                idsToDownload.add(serverIdRev.getKey());
            }
        }
        return idsToDownload;
    }

    private boolean isRevisionMismatch(HashMap<String, String> repoIdsAndRevs, Map.Entry<String, String> serverIdRev) {
        return !repoIdsAndRevs.get(serverIdRev.getKey()).equals(serverIdRev.getValue());
    }

    private boolean isServerIdExistingInRepository(HashMap<String, String> repoIdsAndRevs, Map.Entry<String, String> serverIdRev) {
        return repoIdsAndRevs.get(serverIdRev.getKey()) != null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Toast.makeText(RapidFtrApplication.getApplicationInstance(), values[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
        toggleMenu(SYNC_ALL);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    protected void onCancelled() {
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
        toggleMenu(SYNC_ALL);
        notificationManager.cancel(NOTIFICATION_ID);
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

    void sendChildrenToServer(List<Child> childrenToSyncWithServer) throws IOException, JSONException {
        setProgressAndNotify(context.getString(R.string.synchronize_step_2), formSectionProgress);
        String subStatusFormat = "Uploading Child %s of " + childrenToSyncWithServer.size();
        int counter = 0;
        int startProgress = formSectionProgress;
        for (Child child : childrenToSyncWithServer) {
            if (isCancelled()) {
                break;
            }
            childService.sync(child, currentUser);
            setProgressAndNotify(String.format(subStatusFormat, ++counter), startProgress);
            startProgress += 1;
        }
    }

    protected void saveIncomingChildren(ArrayList<String> idsToDownload, int startProgress) throws IOException, JSONException {
        String subStatusFormat = "Downloading Child %s of" + idsToDownload.size();
        int counter = 0;
        setProgressAndNotify(context.getString(R.string.synchronize_step_3), startProgress);

        for (String idToDownload : idsToDownload) {
            Child incomingChild = childService.getChild(idToDownload);
            if (isCancelled()) {
                break;
            }
            try {
                incomingChild.setSynced(true);
                if (childRepository.exists(incomingChild.getUniqueId())) {
                    childRepository.update(incomingChild);
                } else {
                    childRepository.createOrUpdate(incomingChild);
                }
                childService.setPhoto(incomingChild);
                setProgressAndNotify(String.format(subStatusFormat, ++counter), startProgress);
                startProgress += 1 ;
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
