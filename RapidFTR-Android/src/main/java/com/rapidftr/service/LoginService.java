package com.rapidftr.service;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.rapidftr.utils.http.FluentResponse;

import java.io.IOException;

import static com.rapidftr.utils.http.FluentRequest.http;

public class LoginService {

    public FluentResponse login(Context context, String username, String password, String url) throws IOException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return http()
              .context(context)
              .path("/api/login")
              .host(url)
              .param("imei", android_id)
              .param("mobile_number", telephonyManager.getLine1Number())
              .param("user_name", username)
              .param("password", password)
              .post();
    }

}
