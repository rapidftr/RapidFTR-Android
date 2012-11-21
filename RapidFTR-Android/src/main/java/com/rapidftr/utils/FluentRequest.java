package com.rapidftr.utils;

import android.content.Context;
import android.net.Uri;
import com.google.common.collect.ObjectArrays;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Cleanup;
import lombok.Getter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.*;
import java.util.*;

public class FluentRequest {

    private static @Getter(lazy = true) final HttpClient httpClient = createHttpClient();

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

        return getHttpClient().execute(request);
    }

    public String getBaseUrl(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(RapidFtrApplication.SHARED_PREFERENCES_FILE, 0)
                .getString("SERVER_URL", "");
    }

    public int getConnectionTimeout(Context context) {
        return context.getResources().getInteger(R.integer.http_timeout);
    }

    private static HttpClient createHttpClient() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            @Cleanup InputStream in = RapidFtrApplication.getInstance().getResources().openRawResource(R.raw.trusted);
            trusted.load(in, "rapidftr".toCharArray());

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", new SelfSignedSSLSocketFactory(trusted), 443));

            HttpParams params = new BasicHttpParams();
            ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(connectionManager, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static class SelfSignedSSLSocketFactory extends SSLSocketFactory {

        protected final javax.net.ssl.SSLSocketFactory socketFactory;

        public SelfSignedSSLSocketFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(null, null, null, null, null, null);
            this.setHostnameVerifier(BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

            TrustManagerFactory originalCAs = TrustManagerFactory.getInstance("X509");
            originalCAs.init((KeyStore) null);

            TrustManagerFactory customCAs = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            customCAs.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, ObjectArrays.concat(customCAs.getTrustManagers(), originalCAs.getTrustManagers(), TrustManager.class), null);
            socketFactory = sslContext.getSocketFactory();
        }

        @Override
        public Socket createSocket() throws IOException {
            return socketFactory.createSocket();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return socketFactory.createSocket(socket, host, port, autoClose);
        }
    }

}
