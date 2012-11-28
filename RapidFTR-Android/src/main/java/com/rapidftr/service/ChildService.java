package com.rapidftr.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.CaptureHelper;
import com.rapidftr.utils.JSONArrays;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static java.util.Arrays.asList;

public class ChildService {

    private RapidFtrApplication context;
    private ChildRepository repository;
    private FluentRequest fluentRequest;

    @Inject
    public ChildService(RapidFtrApplication context, ChildRepository repository, FluentRequest fluentRequest) {
        this.context = context;
        this.repository = repository;
        this.fluentRequest = fluentRequest;
    }

    public Child sync(Child child) throws IOException, JSONException {
        String path = child.isNew() ? "/children" : String.format("/children/%s", child.get(internal_id.getColumnName()));

        fluentRequest.path(path).context(context).param("child", child.values().toString());
        addPhotoToTheRequest(child);
        Log.e("######",child.getJsonString());
        FluentResponse response = child.isNew() ? fluentRequest.postWithMultipart() : fluentRequest.put();

        if (response != null && response.isSuccess()) {
            String source = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            child = new Child(source);
            child.setSynced(true);
            child.remove("_attachments");
            repository.update(child);
            setPhoto(child);
            return child;
        } else {
            throw new SyncFailedException(child.getUniqueId());
        }
    }

    private void addPhotoToTheRequest(Child child) throws JSONException {
        if (child.opt("current_photo_key") != null && !child.optString("current_photo_key").equals("")) {
            List<Object> photoKeys = getPhotoKeys(child);
            if (!photoKeys.contains(child.optString("current_photo_key"))) {
                fluentRequest.param("current_photo_key", child.optString("current_photo_key"));
            }
        }
    }

    private List<Object> getPhotoKeys(Child child) throws JSONException {
        JSONArray array = child.has("photo_keys") ? child.getJSONArray("photo_keys") : new JSONArray();
        return JSONArrays.asList(array);
    }

    public void setPrimaryPhoto(Child child, String currentPhotoKey) throws JSONException, IOException {
        //TODO need to modify this code once we implement multiple images. Can clean the webapp API, so that
        //TODO we can send the primary photo along with the child put request

        if(!currentPhotoKey.equals("")){
            String setPrimaryPhotoPath = "/children/"+child.get(internal_id.getColumnName())+"/select_primary_photo/"+currentPhotoKey;
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

    private boolean containsIgnoreCase(String completeString, String subString) {
        return completeString.toLowerCase().contains(subString.toLowerCase());
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

    public void setPhoto(Child child) throws IOException {
        HttpResponse httpResponse = getPhoto(child);
        Bitmap bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent());
        CaptureHelper captureHelper = new CaptureHelper(context);
        String current_photo_key = child.optString("current_photo_key");
        savePhoto(bitmap, captureHelper, current_photo_key);

    }

    private void savePhoto(Bitmap bitmap, CaptureHelper captureHelper, String current_photo_key) throws IOException {
        if(bitmap!=null && !current_photo_key.equals("")){
            try {
                captureHelper.saveThumbnail(bitmap, current_photo_key);
                captureHelper.savePhoto(bitmap, current_photo_key);
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
}
