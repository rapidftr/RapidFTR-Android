package com.rapidftr.http;


import android.content.Context;
import android.telephony.TelephonyManager;
import com.rapidftr.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpClient {
    protected static org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();

    public  HttpResponse post(Context context, String username, String password, String url) throws IOException {
        HttpPost post = new HttpPost(HttpUtils.getFormattedUrl(url) + "/sessions");
        post.addHeader("Accept", "application/json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("user_name", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        nameValuePairs.add(new BasicNameValuePair("imei", telephonyManager.getDeviceId()));
        nameValuePairs.add(new BasicNameValuePair("mobile_number", telephonyManager.getLine1Number()));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return httpClient.execute(post);
    }

    public HttpResponse get(String url) throws IOException {
        HttpGet get = new HttpGet(HttpUtils.getFormattedUrl(url) + "/published_form_sections");
        get.addHeader("Accept", "application/json");
        return httpClient.execute(get);
    }
}
