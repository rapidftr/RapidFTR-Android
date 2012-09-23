package com.rapidftr.service;

import android.content.Context;
import android.telephony.TelephonyManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginService extends AbstractService {

    public HttpResponse login(Context context, String username, String password, String url) throws IOException {
        HttpPost post = new HttpPost(getFormattedUrl(url) + "/sessions");
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

}
