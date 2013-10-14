package com.rapidftr.service;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.http.FluentRequest;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CustomTestRunner.class)
public class DeviceServiceTest {
    private RapidFtrApplication context;
    private TelephonyManager telephonyManager;

    @Before
    public void setUp() {
        context = spy(RapidFtrApplication.getApplicationInstance());
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();

        telephonyManager = spy((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE));
        when(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        when(telephonyManager.getDeviceId()).thenReturn("1234");
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
}
