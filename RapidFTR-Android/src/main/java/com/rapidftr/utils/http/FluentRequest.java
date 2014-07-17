package com.rapidftr.utils.http;

import android.content.Context;
import android.net.Uri;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.IOUtils;
import com.rapidftr.utils.PhotoCaptureHelper;
import lombok.Getter;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.*;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;

public class FluentRequest {

    private static @Getter(lazy = true) final DefaultHttpClient httpClient = createHttpClient();

    protected Map<String, String> headers;
    protected Map<String, String> modelParams;
    protected Map<String, Object> configs;
    protected Uri.Builder uri;
    protected Context context;

    public static FluentRequest http() {
        return new FluentRequest();
    }

    @Inject
    public FluentRequest() {
        reset();
    }

    public FluentRequest host(String host) {
        if (host != null && (host.startsWith("https://") || host.startsWith("http://"))) {
            String[] parts = host.split("\\:\\/\\/");
            scheme(parts[0]);
            host = parts[1];
        } else {
	        scheme("http");
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
        modelParams.put(name, value);
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
        this.context = context;
        host(getBaseUrl(context));
        config(HttpConnectionParams.CONNECTION_TIMEOUT, getConnectionTimeout(context));
	    config(HttpConnectionParams.SO_TIMEOUT, getConnectionTimeout(context));
        return this;
    }

    public FluentResponse get() throws IOException {
        return executeUnenclosed(new HttpGet(uri.build().toString()));
    }

    public FluentResponse post() throws IOException {
        return executeEnclosed(new HttpPost(uri.build().toString()));
    }

    public FluentResponse postWithMultiPart() throws IOException {
        return executeMultiPart(new HttpPost(uri.build().toString()));
    }

    public FluentResponse putWithMultiPart() throws IOException {
        return executeMultiPart(new HttpPut(uri.build().toString()));
    }

    public FluentResponse delete() throws IOException {
        return executeUnenclosed(new HttpDelete(uri.build().toString()));
    }


    protected FluentResponse executeMultiPart(HttpEntityEnclosingRequestBase request) throws IOException{
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        String modelType = "";
        if (modelParams.size() > 0) {
            for (Map.Entry<String, String> modelParam : modelParams.entrySet()){
                if(modelParam.getKey().equals("photo_keys")){
                    addPhoto(multipartEntity, modelType, modelParam);
                }else if(modelParam.getKey().equals("recorded_audio")){
                    addAudio(multipartEntity, modelParam);
                }else{
                    modelType = modelParam.getKey();
                    addTextFields(multipartEntity, modelType, modelParam);
                }
            }
        }
        request.setEntity(multipartEntity);
        return execute(request);
    }

    private void addTextFields(MultipartEntity multipartEntity, String modelType, Map.Entry<String, String> modelParam) throws UnsupportedEncodingException {
        BaseModel baseModel;
        try {
            baseModel = (modelType == "child") ? new Child(modelParam.getValue()) : new Enquiry(modelParam.getValue());
            Iterator keys = baseModel.keys();
            while(keys.hasNext())
            {
                String currentKey = keys.next().toString();
                multipartEntity.addPart(modelType+"["+currentKey+"]", new StringBody(baseModel.get(currentKey).toString(), Charset.defaultCharset()));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void addAudio(MultipartEntity multipartEntity, Map.Entry<String, String> modelParam) {
        try {
            multipartEntity.addPart("child[audio]",
                    new ByteArrayBody(IOUtils.toByteArray(new FileInputStream(new File(new AudioCaptureHelper((RapidFtrApplication) context).getCompleteFileName(modelParam.getValue())))),
                            "audio/amr", modelParam.getValue()+".amr"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addPhoto(MultipartEntity multipartEntity, String param_model, Map.Entry<String, String> param) {
        try {
            addPhotoToMultiPart(multipartEntity, param.getValue(), param_model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addPhotoToMultiPart(MultipartEntity multipartEntity, String param, String model) throws IOException, GeneralSecurityException, JSONException {
        JSONArray photoKeys = new JSONArray(param);
        for(int i = 0; i < photoKeys.length(); i++){
            multipartEntity.addPart(model+"[photo]["+i+"]", attachPhoto(photoKeys.get(i).toString()));
        }
    }

    protected ByteArrayBody attachPhoto(String fileName) throws IOException, GeneralSecurityException {
        return new ByteArrayBody(IOUtils.toByteArray(new PhotoCaptureHelper((RapidFtrApplication) context).getDecodedImageStream(fileName)),
                        "image/jpg", fileName+".jpg");
    }

    protected FluentResponse executeUnenclosed(HttpRequestBase request) throws IOException {
        if (modelParams.size() > 0) {
            for (Map.Entry<String, String> param : modelParams.entrySet())
                uri.appendQueryParameter(param.getKey(), param.getValue());

            request.setURI(URI.create(uri.build().toString()));
        }
        return execute(request);
    }

    protected FluentResponse executeEnclosed(HttpEntityEnclosingRequestBase request) throws IOException {
        if (modelParams.size() > 0) {
            List<BasicNameValuePair> entities = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> param : modelParams.entrySet())
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

    protected FluentResponse execute(HttpRequestBase request) throws IOException {
        for (Map.Entry<String, Object> config : configs.entrySet())
            request.getParams().setParameter(config.getKey(), config.getValue());

        for (Map.Entry<String, String> header : headers.entrySet())
            request.setHeader(header.getKey(), header.getValue());

        reset();
        return new FluentResponse(getHttpClient().execute(request));
    }

    public void reset() {
        headers = new HashMap<String, String>();
        modelParams = new HashMap<String, String>();
        configs = new HashMap<String, Object>();
        uri = new Uri.Builder();
        context = null;

        header("Accept", "application/json");
        scheme("http"); // TODO: Default scheme should be https, but how to specify URL in Login Screen?
        path("/");
    }

    public String getBaseUrl(Context context) {
        return ((RapidFtrApplication) context.getApplicationContext()).getSharedPreferences().getString(SERVER_URL_PREF, null);
    }

    public int getConnectionTimeout(Context context) {
        return context.getResources().getInteger(R.integer.http_timeout);
    }

    private static DefaultHttpClient createHttpClient() {
        try {
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", new SelfSignedSSLSocketFactory(), 443));

            HttpParams params = new BasicHttpParams();
            ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(connectionManager, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
