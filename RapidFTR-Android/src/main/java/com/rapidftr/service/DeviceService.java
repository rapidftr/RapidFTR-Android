package com.rapidftr.service;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.http.FluentResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.rapidftr.utils.http.FluentRequest.http;

public class DeviceService {
    private RapidFtrApplication context;

    @Inject
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

    public void wipeData() {
        if(getDeviceWipeFlag() == true) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
            wipeDirectory(new File(Environment.getExternalStorageDirectory().toString()));
            devicePolicyManager.wipeData(0);
        }
    }

    protected boolean getDeviceWipeFlag(){
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Integer deviceWipeFlag = (Integer)applicationInfo.metaData.get("device.wipe.flag");

            return deviceWipeFlag == 1;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    protected void wipeDirectory(File rootDirectory) {
        File[] listFiles = rootDirectory.listFiles();

        if ( listFiles == null ) return;

        for (File file: listFiles)
        {
            if (file.isDirectory()) {
                wipeDirectory(file);
            }
            file.delete();
        }
    }
}
