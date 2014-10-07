package com.rapidftr.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Map;

import static com.rapidftr.view.fields.PhotoUploadBox.PHOTO_KEYS;

public class MediaSyncHelper {

    private EntityHttpDao<? extends BaseModel> entityHttpDao;
    private RapidFtrApplication context;

    public MediaSyncHelper(EntityHttpDao<? extends BaseModel> entityHttpDao, RapidFtrApplication context) {
        this.entityHttpDao = entityHttpDao;
        this.context = context;
    }

    public void addMultiMediaFilesToTheRequestParameters(BaseModel baseModel, Map<String, String> requestParameters) throws JSONException {
        requestParameters.put("photo_keys", updatedPhotoKeys(baseModel).toString());
        if (baseModel.opt("recorded_audio") != null && !baseModel.optString("recorded_audio").equals("")) {
            if (!getAudioKey(baseModel).equals(baseModel.optString("recorded_audio"))) {
                requestParameters.put("recorded_audio", baseModel.optString("recorded_audio"));
            }
        }
        baseModel.remove("attachments");
    }

    public void setPhoto(BaseModel baseModel) throws IOException, JSONException {
        PhotoCaptureHelper photoCaptureHelper = new PhotoCaptureHelper(context);

        JSONArray photoKeys = baseModel.optJSONArray("photo_keys");
        if (photoKeys != null) {
            getPhotoFromServerIfNeeded(baseModel, photoCaptureHelper, photoKeys);
        }

    }

    private JSONArray updatedPhotoKeys(BaseModel model) throws JSONException {
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

    private String getAudioKey(BaseModel baseModel) throws JSONException {
        return (baseModel.has("audio_attachments") && baseModel.getJSONObject("audio_attachments").has("original")) ? baseModel.getJSONObject("audio_attachments").optString("original") : "";
    }

    private void savePhoto(Bitmap bitmap, PhotoCaptureHelper photoCaptureHelper, String current_photo_key) throws IOException {
        if (bitmap != null && !current_photo_key.equals("")) {
            try {
                photoCaptureHelper.saveThumbnail(bitmap, 0, current_photo_key);
                photoCaptureHelper.savePhoto(bitmap, 0, current_photo_key);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void getPhotoFromServerIfNeeded(BaseModel baseModel, PhotoCaptureHelper photoCaptureHelper, JSONArray photoKeys) throws JSONException, IOException {
        for (int i = 0; i < photoKeys.length(); i++) {
            String photoKey = photoKeys.get(i).toString();
            try {
                if (!photoKey.equals("")) {
                    photoCaptureHelper.getFile(photoKey, ".jpg");
                }
            } catch (FileNotFoundException e) {
                getPhotoFromServer(baseModel, photoCaptureHelper, photoKey);
            }
        }
    }

    public void getPhotoFromServer(BaseModel baseModel, PhotoCaptureHelper photoCaptureHelper, String fileName) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(getReSizedPhoto(baseModel, fileName));
        savePhoto(bitmap, photoCaptureHelper, fileName);
    }

    protected InputStream getReSizedPhoto(BaseModel baseModel, String fileName) throws IOException {
        String apiModel = baseModel.getClass().getSimpleName().toLowerCase();
        String photoUrl =
                String.format("/%s/%s/photo/%s/resized/%sx%s",
                        apiModel, baseModel.optString("_id"), fileName, PhotoCaptureHelper.PHOTO_WIDTH, PhotoCaptureHelper.PHOTO_HEIGHT);

        return entityHttpDao.getResourceStream(photoUrl);
    }

    public void setAudio(BaseModel baseModel) throws IOException, JSONException {
        AudioCaptureHelper audioCaptureHelper = new AudioCaptureHelper(context);
        String recordedAudio = baseModel.optString("recorded_audio");
        try {
            if (!recordedAudio.equals("")) {
                audioCaptureHelper.getFile(recordedAudio, ".amr");
            }
        } catch (FileNotFoundException e) {
            getAudioFromServer(baseModel, audioCaptureHelper);
        }
    }

    private void getAudioFromServer(BaseModel baseModel, AudioCaptureHelper audioCaptureHelper) throws IOException, JSONException {
        audioCaptureHelper.saveAudio(baseModel, getAudio(baseModel));
    }

    public InputStream getAudio(BaseModel baseModel) throws IOException {
        String audioUrlPath = String.format("/api/children/%s/audio", baseModel.optString("_id"));
        return entityHttpDao.getResourceStream(audioUrlPath);
    }
}
