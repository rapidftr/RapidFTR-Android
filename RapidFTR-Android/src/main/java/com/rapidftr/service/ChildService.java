package com.rapidftr.service;

import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.model.Child;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.rapidftr.utils.FluentRequest.http;
import static java.util.Arrays.asList;

public class ChildService {

    private RapidFtrApplication context;

    @Inject
    public ChildService(RapidFtrApplication context) {
        this.context = context;
    }

    public void post(Child child) throws IOException, JSONException {
        HttpResponse formSectionsResponse = http()
                .path("/children")
                .context(context)
                .param("id", child.getId())
                .param("child", child.get(Database.ChildTableColumn.content.getColumnName()).toString())
                .put();

        String formSectionsTemplate = CharStreams.toString(new InputStreamReader(formSectionsResponse.getEntity().getContent()));
        context.setFormSectionsTemplate(formSectionsTemplate);
    }

    public List<Child> getAllChildren() throws IOException {
        HttpResponse response = http()
                .context(context)
                .path("/children")
                .get();

        String childrenJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        return convertToChildRecords(childrenJson);
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
