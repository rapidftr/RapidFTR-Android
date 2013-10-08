package com.rapidftr.utils;

import android.app.admin.DevicePolicyManager;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.DeviceAdminActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static android.content.Context.DEVICE_POLICY_SERVICE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class DeviceAdminTest {
    @Mock private DevicePolicyManager devicePolicyManager;
    private RapidFtrApplication context;

    @Before
    public void setUp(){
        initMocks(this);
        context = spy(RapidFtrApplication.getApplicationInstance());
        given(context.getSystemService(Matchers.anyString())).willReturn(devicePolicyManager);
    }

    @Test
    public void shouldWipeInternalDataFromPhone(){
        DeviceAdmin deviceAdmin = new DeviceAdmin(context);
        deviceAdmin.wipeData();
        verify(devicePolicyManager).wipeData(0);
    }
}
