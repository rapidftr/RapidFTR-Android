package com.rapidftr.utils;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class DeviceAdmin extends DeviceAdminReceiver {

    private Context context;

    public DeviceAdmin(){};
    public DeviceAdmin(Context context) {
        this.context = context;
    }

    public void wipeData() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.wipeData(0);
    }
}
