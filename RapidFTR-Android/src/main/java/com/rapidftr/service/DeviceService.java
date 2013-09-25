package com.rapidftr.service;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.google.common.io.CharStreams;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.rapidftr.utils.http.FluentRequest.http;

public class DeviceService {
    private RapidFtrApplication context;

    public DeviceService(RapidFtrApplication context) {
        this.context = context;
    }

    public Boolean isBlacklisted() throws IOException, JSONException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        FluentResponse response =  http()
                .context(context)
                .path(String.format("/api/is_blacklisted/%s", imei))
                .get();

        String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        JSONObject device = new JSONObject(responseAsString);
        return device.getBoolean("blacklisted");
    }
}
