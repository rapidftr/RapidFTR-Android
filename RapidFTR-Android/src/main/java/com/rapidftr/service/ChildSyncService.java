package com.rapidftr.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.utils.RapidFtrDateTime;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.view.fields.PhotoUploadBox.PHOTO_KEYS;
import static java.util.Arrays.asList;

public class ChildSyncService implements SyncService<Child> {
    public static final String CHILDREN_API_PATH = "/api/children";
    public static final String CHILDREN_API_PARAMETER = "child";

    private RapidFtrApplication context;
    private ChildRepository childRepository;
    private FluentRequest fluentRequest;
    private JSONArray photoKeys;
    private Object audioAttachments;

    private static final int NOTIFICATION_ID = 1022;
    private EntityHttpDao<Child> childEntityHttpDao;


    @Inject
    public ChildSyncService(RapidFtrApplication context, ChildRepository childRepository, FluentRequest fluentRequest) {
        this.context = context;
        this.childRepository = childRepository;
        this.fluentRequest = fluentRequest;
        this.childEntityHttpDao = EntityHttpDaoFactory.createChildHttpDao(
                context.getSharedPreferences().getString(RapidFtrApplication.SERVER_URL_PREF, ""),
                CHILDREN_API_PATH, CHILDREN_API_PARAMETER);
    }

    @Override
    public Child sync(Child child, User currentUser) throws IOException, JSONException {
        addMultiMediaFilesToTheRequest(child);
        removeUnusedParametersBeforeSync(child);
        FluentResponse response = sendToServer(child, currentUser);
        try {
            String source = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

            if (response.isSuccess()) {
                child = new Child(source);
                setChildAttributes(child);
                childRepository.update(child);
                setMedia(child);
                childRepository.close();
                return child;
            } else {
                throw new Exception(source);
            }
        } catch (Exception e) {
            child.setSynced(false);
            child.setSyncLog(e.getMessage());
        }
        return child;
    }

    private FluentResponse sendToServer(Child child, User currentUser) throws JSONException, SyncFailedException {
        fluentRequest.path(getSyncPath(child, currentUser)).context(context).param("child", child.values().toString());
        FluentResponse response;
        try {
            response = child.isNew() ? fluentRequest.postWithMultiPart() : fluentRequest.putWithMultiPart();
        } catch (IOException e) {
            child.setSynced(false);
            child.setSyncLog(e.getMessage());
            child.put("photo_keys", photoKeys);
            child.put("audio_attachments", audioAttachments);
            childRepository.update(child);
            childRepository.close();
            throw new SyncFailedException(e.getMessage());
        }
        return response;
    }

    public String getSyncPath(Child child, User currentUser) throws JSONException {
        if (currentUser.isVerified()) {
            return child.isNew() ? "/api/children" : String.format("/api/children/%s", child.get(internal_id.getColumnName()));
        } else {
            return "/api/children/unverified";
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

    private void setChildAttributes(Child child) throws JSONException {
        child.setSynced(true);
        child.setLastSyncedAt(RapidFtrDateTime.now().defaultFormat());
        child.remove("_attachments");
    }

    private void addMultiMediaFilesToTheRequest(Child child) throws JSONException {
        fluentRequest.param("photo_keys", updatedPhotoKeys(child).toString());
        if (child.opt("recorded_audio") != null && !child.optString("recorded_audio").equals("")) {
            if (!getAudioKey(child).equals(child.optString("recorded_audio"))) {
                fluentRequest.param("recorded_audio", child.optString("recorded_audio"));
            }
        }
        child.remove("attachments");
    }

    private void removeUnusedParametersBeforeSync(Child child) {
        photoKeys = (JSONArray) child.remove("photo_keys");
        audioAttachments = child.remove("audio_attachments");
    }

    private String getAudioKey(Child child) throws JSONException {
        return (child.has("audio_attachments") && child.getJSONObject("audio_attachments").has("original")) ? child.getJSONObject("audio_attachments").optString("original") : "";
    }

    @Override
    public Child getRecord(String id) throws IOException, JSONException {
        HttpResponse response = fluentRequest
                .context(context)
                .path(String.format("/api/children/%s", id))
                .get();

        String childrenJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        Child child = new Child(childrenJson);
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
        HttpResponse httpResponse = getPhoto(child, fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent());
        savePhoto(bitmap, photoCaptureHelper, fileName);
    }

    public HttpResponse getPhoto(Child child, String fileName) throws IOException {
        String photoUrl =
                String.format("/children/%s/photo/%s/resized/%sx%s",
                        child.optString("_id"), fileName, PhotoCaptureHelper.PHOTO_WIDTH, PhotoCaptureHelper.PHOTO_HEIGHT);

        return fluentRequest
                .path(photoUrl)
                .context(context)
                .get();
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
        HttpResponse response = getAudio(child);
        audioCaptureHelper.saveAudio(child, response.getEntity().getContent());
    }

    public HttpResponse issueGetPhotoRequest(BaseModel child, String fileName) throws IOException {
        return fluentRequest
                .path(String.format("/api/children/%s/photo/%s", child.optString("_id"), fileName))
                .context(context)
                .get();
    }

    public HttpResponse getAudio(Child child) throws IOException {
        return fluentRequest
                .path(String.format("/api/children/%s/audio", child.optString("_id")))
                .context(context)
                .get();

    }

    private HashMap<String, String> getAllIdsAndRevs() throws IOException, HttpException {
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpResponse response = fluentRequest.path("/api/children/ids").context(context).get().ensureSuccess();

        List<Map> idRevs = asList(objectMapper.readValue(response.getEntity().getContent(), Map[].class));
        HashMap<String, String> idRevMapping = new HashMap<String, String>();
        for (Map idRev : idRevs) {
            idRevMapping.put(idRev.get("_id").toString(), idRev.get("_rev").toString());
        }
        return idRevMapping;
    }

    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        HashMap<String, String> serverIdsRevs = getAllIdsAndRevs();
        HashMap<String, String> repoIdsAndRevs = childRepository.getAllIdsAndRevs();
        ArrayList<String> idsToDownload = new ArrayList<String>();
        for (Map.Entry<String, String> serverIdRev : serverIdsRevs.entrySet()) {
            if (!(repoIdsAndRevs.get(serverIdRev.getKey()) != null) || (repoIdsAndRevs.get(serverIdRev.getKey()) != null && !repoIdsAndRevs.get(serverIdRev.getKey()).equals(serverIdRev.getValue()))) {
                idsToDownload.add(serverIdRev.getKey());
            }
        }
        return idsToDownload;
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
