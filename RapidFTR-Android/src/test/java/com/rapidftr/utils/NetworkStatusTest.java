package com.rapidftr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(CustomTestRunner.class)
public class NetworkStatusTest {
    RapidFtrApplication context;
    ConnectivityManager connectivityManager;

    @Before
    public void setup() {
        context = RapidFtrApplication.getApplicationInstance();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Test
    public void shouldReturnTrueIfNetworkIsAvailable() {
        shadowOf(connectivityManager.getActiveNetworkInfo()).setConnectionStatus(true);
        assertTrue(NetworkStatus.isOnline(context));
    }

    @Test
    public void shouldReturnFalseIfNetworkIsNotAvailable() {
        shadowOf(connectivityManager.getActiveNetworkInfo()).setConnectionStatus(false);
        assertFalse(NetworkStatus.isOnline(context));
    }
}
