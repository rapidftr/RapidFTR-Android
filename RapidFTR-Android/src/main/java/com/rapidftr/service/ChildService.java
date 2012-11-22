package com.rapidftr.service;

import android.util.Log;
import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.util.ArrayList;
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
        if (child.opt("current_photo_key") != null && !child.optString("current_photo_key").equals("")) {
            fluentRequest.param("current_photo_key", child.optString("current_photo_key"));
        }

        FluentResponse response = child.isNew() ? fluentRequest.post() : fluentRequest.put();

        if (response != null && response.isSuccess()) {
            String source = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            child = new Child(source);
            child.setSynced(true);
            repository.update(child);
            return child;
        } else {
            throw new SyncFailedException(child.getUniqueId());
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
}
