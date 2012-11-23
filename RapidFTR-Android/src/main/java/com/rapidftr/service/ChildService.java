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
import com.rapidftr.utils.FluentRequest;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
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

    public void sync(Child child) throws IOException, JSONException {
        fluentRequest
                .path(String.format("/children/%s", child.getUniqueId()))
                .context(context)
                .param("child", child.toString());
        if (child.opt("current_photo_key") != null && !child.optString("current_photo_key").equals("")) {
            fluentRequest.param("current_photo_key", child.optString("current_photo_key"));
        }
        fluentRequest.put();
        child.setSynced(true);
        repository.update(child);
    }

    public List<Child> getAllChildren() throws IOException {
        HttpResponse response = fluentRequest
                .context(context)
                .path("/children")
                .get();

        String childrenJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        return convertToChildRecords(childrenJson);
    }

    public List<Child> searchChildrenInDB(String subString) {
        List<Child> filteredList = new ArrayList<Child>();
        try {
            for (Child child : repository.getAllChildren()) {
                try {
                    if (containsIgnoreCase(child.getUniqueId(), subString) ||
                            containsIgnoreCase((String) child.get("name"), subString)) {
                        filteredList.add(child);
                    }
                } catch (JSONException e) {
                    Log.e("ChildService", "Error while Searching Children");
                }
            }
        } catch (JSONException e) {
            Log.e("ChildService", "Error while Searching Children");
        }
        return filteredList;
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

    public void setPhoto(Child child) throws IOException, GeneralSecurityException {
        HttpResponse httpResponse = getPhoto(child);
        Bitmap bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent());
        CaptureHelper captureHelper = new CaptureHelper(context);
        String current_photo_key = child.optString("current_photo_key");
        captureHelper.saveThumbnail(bitmap, current_photo_key);
        captureHelper.savePhoto(bitmap, current_photo_key);
    }

    private HttpResponse getPhoto(Child child) throws IOException {
        return fluentRequest
                    .path(String.format("/children/%s/photo/%s", child.optString("_id"), child.optString("current_photo_key")))
                    .context(context)
                    .get();
    }
}
