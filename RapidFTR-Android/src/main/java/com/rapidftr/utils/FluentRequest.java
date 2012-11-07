package com.rapidftr.utils;

import android.content.Context;
import android.net.Uri;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluentRequest {

    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient(); // TODO: Change this to use AndroidHTTPClient to avoid UI thread locks

    private final Map<String, String> headers = new HashMap<String, String>();
    private final Map<String, String> params  = new HashMap<String, String>();
    private final Map<String, Object> configs = new HashMap<String, Object>();
    private final Uri.Builder uri = new Uri.Builder();

    public static FluentRequest http() {
        return new FluentRequest();
    }

    public FluentRequest() {
        header("Accept", "application/json");
        scheme("http"); // TODO: Default scheme should be https, but how to specify URL in Login Screen?
        path("/");
    }

    public FluentRequest host(String host) {
        if (host.startsWith("https://") || host.startsWith("http://")) {
            String[] parts = host.split("\\:\\/\\/");
            scheme(parts[0]);
            host = parts[1];
        }

        uri.encodedAuthority(host);
        return this;
    }

    public FluentRequest scheme(String scheme) {
        uri.scheme(scheme);
        return this;
    }

    public FluentRequest path(String path) {
        uri.path(path);
        return this;
    }

    public FluentRequest param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public FluentRequest header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public FluentRequest config(String name, Object value) {
        configs.put(name, value);
        return this;
    }

    public FluentRequest context(Context context) {
        host(getBaseUrl(context));
        config(HttpConnectionParams.CONNECTION_TIMEOUT, getConnectionTimeout(context));
        return this;
    }

    public HttpResponse get() throws IOException {
        return executeUnenclosed(new HttpGet(uri.build().toString()));
    }

    public HttpResponse post() throws IOException {
        return executeEnclosed(new HttpPost(uri.build().toString()));
    }

    public HttpResponse put() throws IOException {
        return executeEnclosed(new HttpPut(uri.build().toString()));
    }

    public HttpResponse delete() throws IOException {
        return executeUnenclosed(new HttpDelete(uri.build().toString()));
    }

    private HttpResponse executeUnenclosed(HttpRequestBase request) throws IOException {
        if (params.size() > 0) {
            for (Map.Entry<String, String> param : params.entrySet())
                uri.appendQueryParameter(param.getKey(), param.getValue());

            request.setURI(URI.create(uri.build().toString()));
        }

        return execute(request);
    }

    private HttpResponse executeEnclosed(HttpEntityEnclosingRequestBase request) throws IOException {
        if (params.size() > 0) {
            List<BasicNameValuePair> entities = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> param : params.entrySet())
                entities.add(new BasicNameValuePair(param.getKey(), param.getValue()));

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(entities);
                request.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                throw (IOException) new IOException().initCause(e);
            }
        }

        return execute(request);
    }

    private HttpResponse execute(HttpRequestBase request) throws IOException {
        for (Map.Entry<String, Object> config : configs.entrySet())
            request.getParams().setParameter(config.getKey(), config.getValue());

        for (Map.Entry<String, String> header : headers.entrySet())
            request.setHeader(header.getKey(), header.getValue());

        return HTTP_CLIENT.execute(request);
    }

    public String getBaseUrl(Context context) {
        return context.getApplicationContext()
              .getSharedPreferences(RapidFtrApplication.SHARED_PREFERENCES_FILE, 0)
              .getString("SERVER_URL", "");
    }

    public int getConnectionTimeout(Context context) {
        return context.getResources().getInteger(R.integer.http_timeout);
    }

}
