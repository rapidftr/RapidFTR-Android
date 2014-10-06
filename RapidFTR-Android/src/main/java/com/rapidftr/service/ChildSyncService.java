package com.rapidftr.service;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.RapidFtrDateTime;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;

public class ChildSyncService implements SyncService<Child> {
    public static final String CHILDREN_API_PATH = "/api/children";
    public static final String CHILDREN_API_PARAMETER = "child";
    public static final String UNVERIFIED_USER_CHILDREN_API_PATH = "/api/children/unverified";

    private MediaSyncHelper mediaSyncHelper;
    private RapidFtrApplication context;
    private ChildRepository childRepository;
    private JSONArray photoKeys;
    private Object audioAttachments;

    private static final int NOTIFICATION_ID = 1022;
    private EntityHttpDao<Child> childEntityHttpDao;

    @Inject
    public ChildSyncService(RapidFtrApplication context, EntityHttpDao<Child> childHttpDao, ChildRepository childRepository) {
        this.context = context;
        this.childRepository = childRepository;
        this.childEntityHttpDao = childHttpDao;
        this.mediaSyncHelper = new MediaSyncHelper(childEntityHttpDao, context);
    }

    public ChildSyncService(RapidFtrApplication context, EntityHttpDao<Child> childHttpDao, ChildRepository childRepository, MediaSyncHelper mediaSyncHelper) {
        this.context = context;
        this.childRepository = childRepository;
        this.childEntityHttpDao = childHttpDao;
        this.mediaSyncHelper = mediaSyncHelper;
    }

    @Override
    public Child sync(Child child, User currentUser) throws IOException, JSONException {
        try {
            Map<String, String> requestParameters = new HashMap<String, String>();
            mediaSyncHelper.addMultiMediaFilesToTheRequestParameters(child, requestParameters);
            removeUnusedParametersBeforeSync(child);

            child = child.isNew() ? childEntityHttpDao.create(child, getSyncPath(child, currentUser), requestParameters)
                    : childEntityHttpDao.update(child, getSyncPath(child, currentUser), requestParameters);
            setChildAttributes(child);
            child.remove(History.HISTORIES);
            childRepository.createOrUpdateWithoutHistory(child);
            setMedia(child);
            childRepository.close();
        } catch (Exception e) {
            child.setSynced(false);
            child.setSyncLog(e.getMessage());
            child.put("photo_keys", photoKeys);
            child.put("audio_attachments", audioAttachments);
            childRepository.createOrUpdateWithoutHistory(child);
            childRepository.close();
            throw new SyncFailedException(e.getMessage());
        }

        return child;
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
    public void setLastSyncedAt() {
        RapidFtrApplication.getApplicationInstance()
                .getSharedPreferences()
                .edit()
                .putLong(RapidFtrApplication.LAST_CHILD_SYNC, System.currentTimeMillis())
                .commit();
    }

    private void setChildAttributes(Child child) throws JSONException {
        child.setSynced(true);
        child.setLastSyncedAt(RapidFtrDateTime.now().defaultFormat());
        child.remove("_attachments");
    }

    private void removeUnusedParametersBeforeSync(Child child) {
        photoKeys = (JSONArray) child.remove("photo_keys");
        audioAttachments = child.remove("audio_attachments");
        child.remove("synced");
    }

    @Override
    public Child getRecord(String resourceUrl) throws IOException, JSONException, HttpException {
        Child child = childEntityHttpDao.get(resourceUrl);
        setChildAttributes(child);
        return child;
    }

    public InputStream getOriginalPhoto(BaseModel child, String fileName) throws IOException {
        String photoUrlPath = String.format("/api/children/%s/photo/%s", child.optString("_id"), fileName);
        return childEntityHttpDao.getResourceStream(photoUrlPath);
    }

    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        // Default value is currently epoch
        long lastUpdateMillis = context.getSharedPreferences().getLong(RapidFtrApplication.LAST_CHILD_SYNC, 0);
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return childEntityHttpDao.getUpdatedResourceUrls(lastUpdate);
    }

}
