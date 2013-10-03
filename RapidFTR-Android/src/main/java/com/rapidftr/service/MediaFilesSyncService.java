package com.rapidftr.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.rapidftr.view.fields.PhotoUploadBox.PHOTO_KEYS;

public abstract class MediaFilesSyncService {
    public abstract HttpResponse issueGetPhotoRequest(BaseModel model, String fileName) throws IOException;

    public void setPhoto(BaseModel model) throws IOException, JSONException {
        PhotoCaptureHelper photoCaptureHelper = new PhotoCaptureHelper(RapidFtrApplication.getApplicationInstance());

        JSONArray photoKeys = model.optJSONArray("photo_keys");
        if (photoKeys != null) {
            getPhotoFromServerIfNeeded(model, photoCaptureHelper, photoKeys);
        }

    }

    private void getPhotoFromServerIfNeeded(BaseModel model, PhotoCaptureHelper photoCaptureHelper, JSONArray photoKeys) throws JSONException, IOException {
        for (int i = 0; i < photoKeys.length(); i++) {
            String photoKey = photoKeys.get(i).toString();
            try {
                if (!photoKey.equals("")) {
                    photoCaptureHelper.getFile(photoKey, ".jpg");
                }
            } catch (FileNotFoundException e) {
                getPhotoFromServer(model, photoCaptureHelper, photoKey);
            }
        }
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

    public void getPhotoFromServer(BaseModel model, PhotoCaptureHelper photoCaptureHelper, String fileName) throws IOException {
        HttpResponse httpResponse = issueGetPhotoRequest(model, fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent());
        savePhoto(bitmap, photoCaptureHelper, fileName);
    }

}
