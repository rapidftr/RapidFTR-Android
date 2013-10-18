package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.RapidFtrDateTime;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static java.util.Arrays.asList;

public class ChildService extends MediaFilesSyncService {

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
        try {
            if (response != null && response.isSuccess()) {
                String source = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
                child = new Child(source);
                setChildAttributes(child);
                repository.update(child);
                setMedia(child);
                return child;
            }
        } catch (Exception e) {
            child.setSynced(false);
            child.setSyncLog(e.getMessage());
        }
        return child;
    }

    protected String getSyncPath(Child child, User currentUser) throws JSONException {
        if (currentUser.isVerified()) {
            return child.isNew() ? "/api/children" : String.format("/api/children/%s", child.get(internal_id.getColumnName()));
        } else {
            return "/api/children/unverified";
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

    public Child getChild(String id) throws IOException, JSONException {
        HttpResponse response = fluentRequest
                .context(context)
                .path(String.format("/api/children/%s", id))
                .get();

        String childrenJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        Child child = new Child(childrenJson);
        setChildAttributes(child);
        return child;
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

    @Override
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

    public HashMap<String, String> getAllIdsAndRevs() throws IOException, HttpException {
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpResponse response = fluentRequest.path("/api/children/ids").context(context).get().ensureSuccess();
        List<Map> idRevs = asList(objectMapper.readValue(response.getEntity().getContent(), Map[].class));
        HashMap<String, String> idRevMapping = new HashMap<String, String>();
        for (Map idRev : idRevs) {
            idRevMapping.put(idRev.get("_id").toString(), idRev.get("_rev").toString());
        }
        return idRevMapping;
    }
}
