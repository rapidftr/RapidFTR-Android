package com.rapidftr.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.utils.RapidFtrDateTime;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.view.fields.PhotoUploadBox.PHOTO_KEYS;

public class ChildSyncService implements SyncService<Child> {
    public static final String CHILDREN_API_PATH = "/api/children";
    public static final String CHILDREN_API_PARAMETER = "child";
    public static final String UNVERIFIED_USER_CHILDREN_API_PATH = "/api/children/unverified";

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
    }

    @Override
    public Child sync(Child child, User currentUser) throws IOException, JSONException {
        try {
            Map<String, String> requestParameters = new HashMap<String, String>();
            addMultiMediaFilesToTheRequestParameters(child, requestParameters);
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
        setPhoto(child);
        setAudio(child);
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

    private void addMultiMediaFilesToTheRequestParameters(Child child, Map<String, String> requestParameters) throws JSONException {
        requestParameters.put("photo_keys", updatedPhotoKeys(child).toString());
        if (child.opt("recorded_audio") != null && !child.optString("recorded_audio").equals("")) {
            if (!getAudioKey(child).equals(child.optString("recorded_audio"))) {
                requestParameters.put("recorded_audio", child.optString("recorded_audio"));
            }
        }
        child.remove("attachments");
    }

    private void removeUnusedParametersBeforeSync(Child child) {
        photoKeys = (JSONArray) child.remove("photo_keys");
        audioAttachments = child.remove("audio_attachments");
        child.remove("synced");
    }

    private String getAudioKey(Child child) throws JSONException {
        return (child.has("audio_attachments") && child.getJSONObject("audio_attachments").has("original")) ? child.getJSONObject("audio_attachments").optString("original") : "";
    }

    @Override
    public Child getRecord(String resourceUrl) throws IOException, JSONException, HttpException {
        Child child = childEntityHttpDao.get(resourceUrl);
        setChildAttributes(child);
        return child;
    }

    private void setPhoto(Child child) throws IOException, JSONException {
        PhotoCaptureHelper photoCaptureHelper = new PhotoCaptureHelper(context);

        JSONArray photoKeys = child.optJSONArray("photo_keys");
        if (photoKeys != null) {
            getPhotoFromServerIfNeeded(child, photoCaptureHelper, photoKeys);
        }

    }

    private void getPhotoFromServerIfNeeded(Child child, PhotoCaptureHelper photoCaptureHelper, JSONArray photoKeys) throws JSONException, IOException {
        for (int i = 0; i < photoKeys.length(); i++) {
            String photoKey = photoKeys.get(i).toString();
            try {
                if (!photoKey.equals("")) {
                    photoCaptureHelper.getFile(photoKey, ".jpg");
                }
            } catch (FileNotFoundException e) {
                getPhotoFromServer(child, photoCaptureHelper, photoKey);
            }
        }
    }

    public void getPhotoFromServer(Child child, PhotoCaptureHelper photoCaptureHelper, String fileName) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(getReSizedPhoto(child, fileName));
        savePhoto(bitmap, photoCaptureHelper, fileName);
    }

    public InputStream getReSizedPhoto(Child child, String fileName) throws IOException {
        String photoUrl =
                String.format("/children/%s/photo/%s/resized/%sx%s",
                        child.optString("_id"), fileName, PhotoCaptureHelper.PHOTO_WIDTH, PhotoCaptureHelper.PHOTO_HEIGHT);

        return childEntityHttpDao.getResourceStream(photoUrl);
    }

    private void setAudio(Child child) throws IOException, JSONException {
        AudioCaptureHelper audioCaptureHelper = new AudioCaptureHelper(context);
        String recordedAudio = child.optString("recorded_audio");
        try {
            if (!recordedAudio.equals("")) {
                audioCaptureHelper.getFile(recordedAudio, ".amr");
            }
        } catch (FileNotFoundException e) {
            getAudioFromServer(child, audioCaptureHelper);
        }
    }

    private void getAudioFromServer(Child child, AudioCaptureHelper audioCaptureHelper) throws IOException, JSONException {
        audioCaptureHelper.saveAudio(child, getAudio(child));
    }

    public InputStream getOriginalPhoto(BaseModel child, String fileName) throws IOException {
        String photoUrlPath = String.format("/api/children/%s/photo/%s", child.optString("_id"), fileName);
        return childEntityHttpDao.getResourceStream(photoUrlPath);
    }

    public InputStream getAudio(Child child) throws IOException {
        String audioUrlPath = String.format("/api/children/%s/audio", child.optString("_id"));
        return childEntityHttpDao.getResourceStream(audioUrlPath);
    }

    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        // Default value is currently epoch
        long lastUpdateMillis = context.getSharedPreferences().getLong(RapidFtrApplication.LAST_CHILD_SYNC, 0);
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return childEntityHttpDao.getUpdatedResourceUrls(lastUpdate);
    }

    protected JSONArray updatedPhotoKeys(BaseModel model) throws JSONException {
        JSONArray photoKeys = model.optJSONArray(PHOTO_KEYS);
        JSONArray photoKeysToAdd = new JSONArray();
        if (photoKeys != null) {
            for (int i = 0; i < photoKeys.length(); i++) {
                if (!photoKeys.optString(i).startsWith("photo-")) {
                    photoKeysToAdd.put(photoKeys.optString(i));
                }
            }
        }
        return photoKeysToAdd;
    }

    public void savePhoto(Bitmap bitmap, PhotoCaptureHelper photoCaptureHelper, String current_photo_key) throws IOException {
        if (bitmap != null && !current_photo_key.equals("")) {
            try {
                photoCaptureHelper.saveThumbnail(bitmap, 0, current_photo_key);
                photoCaptureHelper.savePhoto(bitmap, 0, current_photo_key);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
