package com.rapidftr.activity;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.rapidftr.BuildConfig;
import com.rapidftr.utils.DeviceAdmin;

public class DeviceAdminActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        if(!devicePolicyManager.isAdminActive(null)) {
            requestDeviceAdminPermissions();
        }
        else {
            displayLoginScreen();
        }
    }

    protected void requestDeviceAdminPermissions() {
        ComponentName deviceAdmin = new ComponentName(this, DeviceAdmin.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Process will remove device data in case of robbery.");
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);

        startActivityForResult(intent, 1);
    }

    protected void displayLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        displayLoginScreen();
    }
}
