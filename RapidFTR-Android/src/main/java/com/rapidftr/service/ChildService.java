package com.rapidftr.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.JSONArrays;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.utils.RapidFtrDateTime;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.view.fields.PhotoUploadBox.PHOTO_KEYS;
import static java.util.Arrays.asList;

public class ChildService {

    private RapidFtrApplication context;
    private ChildRepository repository;
    private FluentRequest fluentRequest;
    private JSONArray photoKeys;
    private Object audioAttachments;

    @Inject
    public ChildService(RapidFtrApplication context, ChildRepository repository, FluentRequest fluentRequest) {
        this.context = context;
        this.repository = repository;
        this.fluentRequest = fluentRequest;
    }

    public Child sync(Child child, User currentUser) throws IOException, JSONException {
        addMultiMediaFilesToTheRequest(child);
        removeUnusedParametersBeforeSync(child);
        fluentRequest.path(getSyncPath(child, currentUser)).context(context).param("child", child.values().toString());
        FluentResponse response;
        try {
            response = child.isNew() ? fluentRequest.postWithMultipart() : fluentRequest.put();
        } catch (IOException e) {
            child.setSynced(false);
            child.setSyncLog(e.getMessage());
            child.put("photo_keys", photoKeys);
            child.put("audio_attachments", audioAttachments);
            repository.update(child);
            throw new SyncFailedException(e.getMessage());
        }
        if (response != null && response.isSuccess()) {
            String source = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            child = new Child(source);
            setChildAttributes(child);
            repository.update(child);
            setMedia(child);
            return child;
        } else {
            throw new SyncFailedException(child.getUniqueId());
        }
    }
    
    protected String getSyncPath(Child child, User currentUser) throws JSONException {
        if (currentUser.isVerified()) {
            return child.isNew() ? "/children" : String.format("/children/%s", child.get(internal_id.getColumnName()));
        } else {
            return "/children/sync_unverified";
        }
    }

    private void setMedia(Child child) throws IOException, JSONException {
        setPhoto(child);
        setAudio(child);
    }

    private void setChildAttributes(Child child) throws JSONException {
        child.setSynced(true);
        child.setLastSyncedAt(RapidFtrDateTime.now().defaultFormat());
        child.remove("_attachments");
    }

    private void addMultiMediaFilesToTheRequest(Child child) throws JSONException {
        JSONArray photoKeys = child.optJSONArray(PHOTO_KEYS);
        JSONArray photoKeysToAdd = new JSONArray();
        if(photoKeys != null){
            for(int i = 0; i< photoKeys.length(); i++){
                if(!photoKeys.optString(i).startsWith("photo-")){
                    photoKeysToAdd.put(photoKeys.optString(i));
                }
            }
            fluentRequest.param("photo_keys", photoKeysToAdd.toString());
        }
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

    private List<Object> getPhotoKeys(Child child) throws JSONException {
        JSONArray array = child.has("photo_keys") ? child.getJSONArray("photo_keys") : new JSONArray();
        return JSONArrays.asList(array);
    }

    private String getAudioKey(Child child) throws JSONException {
        return (child.has("audio_attachments") && child.getJSONObject("audio_attachments").has("original")) ? child.getJSONObject("audio_attachments").optString("original") : "";
    }

    public void setPrimaryPhoto(Child child, String currentPhotoKey) throws JSONException, IOException {
        //TODO need to modify this code once we implement multiple images. Can clean the webapp API, so that
        //TODO we can send the primary photo along with the child put request

        if (!currentPhotoKey.equals("")) {
            String setPrimaryPhotoPath = "/children/" + child.get(internal_id.getColumnName()) + "/select_primary_photo/" + currentPhotoKey;
            fluentRequest.path(setPrimaryPhotoPath);
            fluentRequest.put();
        }
    }

    public List<Child> getAllChildren() throws IOException {
        HttpResponse response = fluentRequest
                .context(context)
                .path("/children")
                .get();

        String childrenJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        return convertToChildRecords(childrenJson);
    }

    public Child getChild(String id) throws IOException, JSONException {
        HttpResponse response = fluentRequest
                .context(context)
                .path(String.format("/children/%s", id))
                .get();

        String childrenJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        Child child = new Child(childrenJson);
        setChildAttributes(child);
        return child;
    }

    private List<Child> convertToChildRecords(String childrenJson) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<Map> childrenJsonData = asList(objectMapper.readValue(childrenJson, Map[].class));

        return newArrayList(transform(childrenJsonData, new Function<Map, Child>() {
            public Child apply(Map content) {
                try {
                    return new Child(objectMapper.writeValueAsString(content));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    public void setPhoto(Child child) throws IOException, JSONException {
        PhotoCaptureHelper photoCaptureHelper = new PhotoCaptureHelper(context);

        JSONArray photoKeys = new JSONArray(child.optString("photo_keys"));
        for(int i = 0; i < photoKeys.length(); i++){
            String photoKey = photoKeys.get(i).toString();
            try {
                if (!photoKey.equals("")) {
                    photoCaptureHelper.getFile(photoKey, ".jpg");
                }
            }
            catch (FileNotFoundException e) {
                getPhotoFromServer(child, photoCaptureHelper, photoKey);
            }
        }

    }

    public void getPhotoFromServer(Child child, PhotoCaptureHelper photoCaptureHelper, String currentPhotoKey) throws IOException {
        HttpResponse httpResponse = getPhoto(child);
        Bitmap bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent());
        savePhoto(bitmap, photoCaptureHelper, currentPhotoKey);
    }


    public void setAudio(Child child) throws IOException, JSONException {
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

    public HttpResponse getPhoto(Child child) throws IOException {
        return fluentRequest
                .path(String.format("/children/%s/photo/%s", child.optString("_id"), child.optString("current_photo_key")))
                .context(context)
                .get();
    }

    public HttpResponse getAudio(Child child) throws IOException {
        return fluentRequest
                .path(String.format("/children/%s/audio", child.optString("_id")))
                .context(context)
                .get();

    }

    public HashMap<String, String> getAllIdsAndRevs() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpResponse response = fluentRequest.path("/children-ids").context(context).get();
        List<Map> idRevs = asList(objectMapper.readValue(response.getEntity().getContent(), Map[].class));
        HashMap<String, String> idRevMapping = new HashMap<String, String>();
        for (Map idRev : idRevs) {
            idRevMapping.put(idRev.get("_id").toString(), idRev.get("_rev").toString());
        }
        return idRevMapping;
    }
}
