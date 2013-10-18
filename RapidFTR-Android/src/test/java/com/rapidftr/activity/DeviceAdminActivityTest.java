package com.rapidftr.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.rapidftr.CustomTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowIntent;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.mockito.Mock;

import static android.app.admin.DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN;
import static android.content.Context.DEVICE_POLICY_SERVICE;
import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class DeviceAdminActivityTest {
    @Mock private DevicePolicyManager devicePolicyManager;
    private DeviceAdminActivity deviceAdminActivity;

    @Before
    public void setUp(){
        initMocks(this);
        deviceAdminActivity = spy(new DeviceAdminActivity());
        given(deviceAdminActivity.getSystemService(DEVICE_POLICY_SERVICE)).willReturn(devicePolicyManager);
    }

    @Test
    public void shouldPromptUserForDeviceAdminPermissionsIfAdminDeviceIsDisabled(){
        when(devicePolicyManager.isAdminActive((ComponentName) anyObject())).thenReturn(false);
        deviceAdminActivity.onCreate(null);
        verify(devicePolicyManager).isAdminActive((ComponentName) anyObject());
        verify(deviceAdminActivity).requestDeviceAdminPermissions();
    }

    @Test
    public void shouldDisplayLoginActivityIfDeviceAdminPermissionIsEnabled() {
        when(devicePolicyManager.isAdminActive((ComponentName) anyObject())).thenReturn(true);
        deviceAdminActivity.onCreate(null);
        verify(devicePolicyManager).isAdminActive((ComponentName) anyObject());
        verify(deviceAdminActivity).displayLoginScreen();
    }

    @Test
    public void shouldDisplayLoginWhenDeviceAdminPermissionsAreDeclined(){
        deviceAdminActivity.onActivityResult(1, Activity.RESULT_CANCELED, null);
        verify(deviceAdminActivity).displayLoginScreen();
    }


    @Test
    public void shouldDisplayLoginWhenDeviceAdminPermissionsAreAccepted(){
        deviceAdminActivity.onActivityResult(1, Activity.RESULT_OK, null);
        verify(deviceAdminActivity).displayLoginScreen();
    }

    @Test
    public void shouldStartDeviceAdminWhenRequested(){
        when(devicePolicyManager.isAdminActive((ComponentName) anyObject())).thenReturn(false);

        ShadowActivity shadowActivity = shadowOf(deviceAdminActivity);
        deviceAdminActivity.requestDeviceAdminPermissions();
        ShadowIntent shadowIntent = shadowOf(shadowActivity.getNextStartedActivity());

        verify(deviceAdminActivity).startActivityForResult(Matchers.any(Intent.class), Matchers.anyInt());
        assertThat(shadowIntent.getAction(), equalTo(ACTION_ADD_DEVICE_ADMIN));

        ComponentName componentName = (ComponentName)shadowIntent.getExtras().get(DevicePolicyManager.EXTRA_DEVICE_ADMIN);
        assertThat(componentName.getClass().toString(), equalTo(ComponentName.class.toString()));
        assertThat(shadowIntent.getExtras().get(DevicePolicyManager.EXTRA_ADD_EXPLANATION).toString(),
                equalTo("Process will remove device data in case of robbery."));
    }

    @Test
    public void shouldStartLoginActivityWhenRequested(){
        when(devicePolicyManager.isAdminActive((ComponentName) anyObject())).thenReturn(true);

        ShadowActivity shadowActivity = shadowOf(deviceAdminActivity);
        deviceAdminActivity.displayLoginScreen();
        ShadowIntent shadowIntent = shadowOf(shadowActivity.getNextStartedActivity());

        assertEquals(shadowIntent.getFlags(), Intent.FLAG_ACTIVITY_CLEAR_TOP);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(LoginActivity.class.getName()));
    }

}
