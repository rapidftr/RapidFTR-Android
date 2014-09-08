package com.rapidftr.service;

import android.util.Log;
import com.google.common.io.CharStreams;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rapidftr.utils.http.FluentRequest.http;

public class EntityHttpDao<T extends BaseModel> {

    private static final String UPDATED_AFTER_FORM_PARAMETER = "updated_after";
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String LOCATION_ATTRIBUTE = "location";
    private static final String CHARACTER_SET = "UTF-8";

    private String serverUrl;
    private String apiPath;
    private String apiParamter;

    public EntityHttpDao() {
        this.serverUrl = RapidFtrApplication.getApplicationInstance().getCurrentUser().getServerUrl();
    }

    public EntityHttpDao(String serverUrl, String apiPath, String apiParamter) {
        this();
        this.serverUrl = serverUrl;
        this.apiPath = apiPath;
        this.apiParamter = apiParamter;
    }

    public T get(String resourceUrl) throws IOException, HttpException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(resourceUrl)
                .get()
                .ensureSuccess();

        return buildEntityFromJson(getJsonResponse(fluentResponse));
    }


    public T update(T entity) throws IOException, JSONException, HttpException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(buildUrl())
                .param(apiParamter, entity.values().toString())
                .putWithMultiPart()
                .ensureSuccess();

        return buildEntityFromJson(getJsonResponse(fluentResponse));
    }

    public List<String> getUpdatedResourceUrls(DateTime lastUpdate) throws IOException, HttpException, JSONException {
        String utcString = new StringBuilder(DateTimeFormat.forPattern(DATE_PATTERN).withZone(DateTimeZone.UTC).print(lastUpdate)).append("UTC").toString();
        try {
            final FluentResponse fluentResponse = http().context(RapidFtrApplication.getApplicationInstance())
                    .host(buildUrl())
                    .param(UPDATED_AFTER_FORM_PARAMETER, URLEncoder.encode(utcString, CHARACTER_SET))
                    .get()
                    .ensureSuccess();
            String json = getJsonResponse(fluentResponse);
            JSONArray jsonArray = new JSONArray(json);
            List<String> urls = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                urls.add(jsonArray.getJSONObject(i).getString(LOCATION_ATTRIBUTE));
            }
            return urls;
        } catch (Exception e) {
            Log.e(null, e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    public T create(T entity) throws IOException, HttpException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(buildUrl())
                .param(apiParamter, entity.getJsonString())
                .post()
                .ensureSuccess();

        return buildEntityFromJson(getJsonResponse(fluentResponse));
    }

    private String getJsonResponse(FluentResponse fluentResponse) throws IOException {
        return CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
    }

    private String buildUrl() {
        return new StringBuilder(serverUrl).append(apiPath).toString();
    }

    private T buildEntityFromJson(String jsonResponse) {
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