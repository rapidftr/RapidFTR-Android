package com.rapidftr.service;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.DeviceAdmin;
import com.rapidftr.utils.http.FluentRequest;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.IOException;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class DeviceServiceTest {
    @Mock private DevicePolicyManager devicePolicyManager;
    private RapidFtrApplication context;
    private TelephonyManager telephonyManager;

    @Before
    public void setUp() {
        initMocks(this);
        context = spy(RapidFtrApplication.getApplicationInstance());
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();

        telephonyManager = spy((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE));
        when(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        when(telephonyManager.getDeviceId()).thenReturn("1234");
        given(context.getSystemService(Context.DEVICE_POLICY_SERVICE)).willReturn(devicePolicyManager);

    }

    @Test
    public void shouldSetBlacklistedFlagToTrueIfDeviceBlacklistedRequestIsTrue() throws IOException, JSONException {

        getFakeHttpLayer()
                .addHttpResponseRule("GET",
                "http://whatever/api/is_blacklisted/1234",
                new TestHttpResponse(200, "{\"blacklisted\":\"true\"}"));

        DeviceService service = new DeviceService(context);
        assertTrue(service.isBlacklisted());
    }

    @Test
    public void shouldSetBlacklistedFlagToFalseIfDeviceBlacklistedRequestIsFalse() throws IOException, JSONException {
        getFakeHttpLayer()
                .addHttpResponseRule("GET",
                        "http://whatever/api/is_blacklisted/1234",
                        new TestHttpResponse(200, "{\"blacklisted\":\"false\"}"));

        DeviceService service = new DeviceService(context);
        assertFalse(service.isBlacklisted());
    }

    @Test
    public void shouldNotWipeInternalDataFromPhoneIfWipeDeviceFlagIsFalse(){
        DeviceService service = spy(new DeviceService(context));
        doReturn(false).when(service).getDeviceWipeFlag();

        service.wipeData();

        verify(devicePolicyManager, never()).wipeData(0);
    }

    @Test
    public void shouldWipeInternalDataFromPhoneIfWipeDeviceFlagIsTrue(){
        DeviceService service = spy(new DeviceService(context));
        doReturn(true).when(service).getDeviceWipeFlag();

        service.wipeData();

        verify(devicePolicyManager).wipeData(0);
    }
}
