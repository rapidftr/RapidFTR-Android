package com.rapidftr.service;

import android.util.Log;
import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.rapidftr.utils.FluentRequest.http;
import static java.util.Arrays.asList;

public class ChildService {

    private RapidFtrApplication context;
    private ChildRepository repository;

    @Inject
    public ChildService(RapidFtrApplication context, ChildRepository repository) {
        this.context = context;
        this.repository = repository;
    }

    public void sync(Child child) throws IOException, JSONException {
        child.setSynced(true);
        http()
                .path(String.format("/children/%s", child.getId()))
                .context(context)
                .param("child", child.toString())
                .put();
        repository.update(child);
    }

    public List<Child> getAllChildren() throws IOException {
        HttpResponse response = http()
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
                if (containsIgnoreCase(child.getId(), subString) ||
                        containsIgnoreCase((String) child.get("name"), subString)) {
                    filteredList.add(child);
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
