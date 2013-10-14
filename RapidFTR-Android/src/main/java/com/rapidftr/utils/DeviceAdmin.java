package com.rapidftr.utils;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.google.inject.Inject;


public class DeviceAdmin extends DeviceAdminReceiver {

    private Context context;

    @Inject
    public DeviceAdmin(Context context) {
        this.context = context;
    }

    public void wipeData() {
        if(getDeviceWipeFlag() == true) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.wipeData(0);
        }
    }

    protected boolean getDeviceWipeFlag(){
        try {
            ApplicationInfo  applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Integer deviceWipeFlag = (Integer)applicationInfo.metaData.get("device.wipe.flag");

            return deviceWipeFlag == 1;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
