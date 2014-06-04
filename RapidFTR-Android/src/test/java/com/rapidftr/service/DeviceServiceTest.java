package com.rapidftr.service;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.tester.org.apache.http.TestHttpResponse;

import java.io.File;
import java.io.IOException;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class DeviceServiceTest {
    @Mock private DevicePolicyManager devicePolicyManager;
    private RapidFtrApplication context;
    private TelephonyManager telephonyManager;
    private DeviceService deviceService;

    @Before
    public void setUp() {
        initMocks(this);
        context = spy(RapidFtrApplication.getApplicationInstance());
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        deviceService = spy(new DeviceService(context));

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

        assertTrue(deviceService.isBlacklisted());
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
        doReturn(false).when(deviceService).getDeviceWipeFlag();

        deviceService.wipeData();

        verify(devicePolicyManager, never()).wipeData(0);
    }

    @Test
    public void shouldNotCallWipeExternalSdCardIfWipeDeviceFlagIsFlag() {
        doReturn(false).when(deviceService).getDeviceWipeFlag();
        deviceService.wipeData();

        verify(deviceService, never()).wipeDirectory(any(File.class));
    }

    @Test
    public void shouldWipeInternalDataFromPhoneIfWipeDeviceFlagIsTrue(){
        doReturn(true).when(deviceService).getDeviceWipeFlag();

        deviceService.wipeData();

        verify(devicePolicyManager).wipeData(0);
    }

    @Test
    public void shouldCallWipeExternalSdCardFromPhoneIfWipeDeviceFlagIsTrue(){
        doReturn(true).when(deviceService).getDeviceWipeFlag();
        doNothing().when(deviceService).wipeDirectory(any(File.class));

        deviceService.wipeData();

        verify(deviceService).wipeDirectory(new File(Environment.getExternalStorageDirectory().toString()));
    }

    @Test
    public void shouldWipeExternalSdCardWhenRequested(){
        File root = Mockito.mock(File.class);
        File picturesDirectory = Mockito.mock(File.class);
        File pictureFile = Mockito.mock(File.class);
        File rootFile = Mockito.mock(File.class);


        File[] listFiles = new File[2];
        listFiles[0] = picturesDirectory;
        listFiles[1] = rootFile;
        File[] listFilesPictures = new File[1];
        listFilesPictures[0] = pictureFile;

        doReturn(listFiles).when(root).listFiles();
        doReturn(listFilesPictures).when(picturesDirectory).listFiles();
        doReturn(true).when(picturesDirectory).isDirectory();

        deviceService.wipeDirectory(root);

        verify(rootFile).delete();
        verify(pictureFile).delete();
        verify(picturesDirectory).delete();

    }

    @Test
    public void shouldNotIterateFileListIfDirectoryIsEmpty()
    {
        File root = Mockito.mock(File.class);
        File emptyDirectory = Mockito.mock(File.class);

        File[] filesList = new File[1];
        filesList[0] = emptyDirectory;

        doReturn(filesList).when(root).listFiles();
        doReturn(null).when(emptyDirectory).listFiles();
        doReturn(true).when(emptyDirectory).isDirectory();

        deviceService.wipeDirectory(root);

        verify(emptyDirectory).delete();
    }
}
