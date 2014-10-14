package com.rapidftr.service;

import android.util.Log;
import com.google.common.io.CharStreams;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.rapidftr.utils.http.FluentRequest.http;

public class EntityHttpDao<T extends BaseModel> {

    private static final String UPDATED_AFTER_FORM_PARAMETER = "updated_after";
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String LOCATION_ATTRIBUTE = "location";

    protected String serverUrl;
    protected String apiPath;
    protected String apiParameter;

    public EntityHttpDao() {
        this.serverUrl = RapidFtrApplication.getApplicationInstance().getCurrentUser().getServerUrl();
    }

    public EntityHttpDao(String serverUrl, String apiPath, String apiParameter) {
        this();
        this.serverUrl = serverUrl;
        this.apiPath = apiPath;
        this.apiParameter = apiParameter;
    }

    public T get(String resourceUrl) throws IOException, HttpException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(resourceUrl)
                .get()
                .ensureSuccess();

        return buildEntityFromJson(getJsonResponse(fluentResponse));
    }

    public InputStream getResourceStream(String resourcePath) throws IOException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .path(resourcePath)
                .get();

        return fluentResponse.getEntity().getContent();
    }

    public T update(T entity, String path, Map<String, String> requestParameters) throws IOException, HttpException {
        FluentRequest fluentRequest = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .path(path)
                .param(apiParameter, entity.getJsonString());

        if (requestParameters != null && requestParameters.size() > 0) {
            Iterator<String> keys = requestParameters.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                fluentRequest.param(key, requestParameters.get(key));
            }
        }

        FluentResponse fluentResponse = fluentRequest.putWithMultiPart().ensureSuccess();
        return buildEntityFromJson(getJsonResponse(fluentResponse));
    }

    public List<String> getUpdatedResourceUrls(DateTime lastUpdate) throws IOException, HttpException, JSONException {
        String utcString = new StringBuilder(DateTimeFormat.forPattern(DATE_PATTERN).withZone(DateTimeZone.UTC).print(lastUpdate)).append("UTC").toString();
        final FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(serverUrl)
                .path(apiPath)
                .param(UPDATED_AFTER_FORM_PARAMETER, utcString)
                .get()
                .ensureSuccess();
        String json = getJsonResponse(fluentResponse);
        JSONArray jsonArray = new JSONArray(json);
        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            urls.add(jsonArray.getJSONObject(i).getString(LOCATION_ATTRIBUTE));
        }
        return urls;
    }

    public T create(T entity, String path, Map<String, String> requestParameters) throws IOException, HttpException {
        FluentRequest fluentRequest = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .path(path)
                .param(apiParameter, entity.getJsonString());

        if (requestParameters != null && requestParameters.size() > 0) {
            Iterator<String> keys = requestParameters.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                fluentRequest.param(key, requestParameters.get(key));
            }
        }

        FluentResponse fluentResponse = fluentRequest.postWithMultiPart().ensureSuccess();
        return buildEntityFromJson(getJsonResponse(fluentResponse));
    }

    protected String getJsonResponse(FluentResponse fluentResponse) throws IOException {
        return CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
    }

    private String buildUpdatePath(T entity) {
        StringBuilder builder = new StringBuilder();
        builder.append(apiPath.endsWith("/") ? apiPath : apiPath + "/");
        builder.append(entity.getInternalId() != null ? entity.getInternalId() : "");

        return builder.toString();
    }

    protected T buildEntityFromJson(String jsonResponse) {
        try {
            return (T) getGenericParameterClass().getConstructor(String.class).newInstance(jsonResponse);
        } catch (Exception e) {
            Log.e(null, e.getMessage(), e);
        }

        return null;
    }

    protected Class getGenericParameterClass() {
        return (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}