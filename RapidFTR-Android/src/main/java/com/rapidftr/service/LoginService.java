package com.rapidftr.service;

import android.content.Context;
import android.telephony.TelephonyManager;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static com.rapidftr.utils.http.FluentRequest.http;

public class LoginService {

    public HttpResponse login(Context context, String username, String password, String url) throws IOException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return http()
              .context(context)
              .path("/api/login")
              .host(url)
              .param("imei", telephonyManager.getDeviceId())
              .param("mobile_number", telephonyManager.getLine1Number())
              .param("user_name", username)
              .param("password", password)
              .post();
    }

}
