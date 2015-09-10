package com.rapidftr.service;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Long;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;

public class ChildSyncService implements SyncService<Child> {
    public static final String CHILDREN_API_PATH = "/api/children";
    public static final String CHILDREN_API_PARAMETER = "child";
    public static final String UNVERIFIED_USER_CHILDREN_API_PATH = "/api/children/unverified";

    private MediaSyncHelper mediaSyncHelper;
    private RapidFtrApplication context;
    private ChildRepository childRepository;

    private static final int NOTIFICATION_ID = 1022;
    private EntityHttpDao<Child> childEntityHttpDao;

    @Inject
    public ChildSyncService(RapidFtrApplication context, EntityHttpDao<Child> childHttpDao, ChildRepository childRepository) {
        this.context = context;
        this.childRepository = childRepository;
        this.childEntityHttpDao = childHttpDao;
        this.mediaSyncHelper = new MediaSyncHelper(childEntityHttpDao, context);
    }

    @Override
    public Child sync(Child child, User currentUser) throws IOException, JSONException {
        String syncPath = getSyncPath(child, currentUser);
        GenericSyncService<Child> syncService = new GenericSyncService<Child>(mediaSyncHelper, childEntityHttpDao, childRepository);
        return syncService.sync(child, syncPath);
    }

    public String getSyncPath(Child child, User currentUser) throws JSONException {
        if (currentUser.isVerified()) {
            return child.isNew() ? CHILDREN_API_PATH :
                    new StringBuilder(CHILDREN_API_PATH)
                            .append("/").append(child.get(internal_id.getColumnName())).toString();
        } else {
            return UNVERIFIED_USER_CHILDREN_API_PATH;
        }
    }

    @Override
    public void setMedia(Child child) throws IOException, JSONException {
        mediaSyncHelper.setPhoto(child);
        mediaSyncHelper.setAudio(child);
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public String getNotificationTitle() {
        return context.getString(R.string.child_sync_title);
    }

    @Override
    public void setLastSyncedAt(Child child, boolean isLastRecord) {
        Long lastSynced = null;

        if (isLastRecord) {
            lastSynced = System.currentTimeMillis();
        } else {
            lastSynced = child.lastUpdatedAtInMillis();
        }

        if (lastSynced == null) {
            return;
        }

        RapidFtrApplication.getApplicationInstance()
                .getSharedPreferences()
                .edit()
                .putLong(RapidFtrApplication.LAST_CHILD_SYNC, lastSynced)
                .commit();
    }

    @Override
    public Child getRecord(String resourceUrl) throws IOException, JSONException, HttpException {
        Child child = childEntityHttpDao.get(resourceUrl);
        GenericSyncService.setAttributes(child);
        return child;
    }

    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        // Default value is currently epoch
        long lastUpdateMillis = context.getSharedPreferences().getLong(RapidFtrApplication.LAST_CHILD_SYNC, 0);
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return childEntityHttpDao.getUpdatedResourceUrls(lastUpdate);
    }

}
