package com.rapidftr.bean;

import android.app.ProgressDialog;
import android.content.Context;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.task.MigrateUnverifiedDataToVerified;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@MockPolicy(AndroidMockPolicy.class)
public class LoginTaskTest {

    ConnectivityBean connectivityBean = mock(ConnectivityBean.class, RETURNS_DEEP_STUBS);
    RapidFtrApplication rapidFtrApplication = mock(RapidFtrApplication.class, RETURNS_DEEP_STUBS);
    Context activity = mock(Context.class, RETURNS_DEEP_STUBS);
    ProgressDialog progressDialog = mock(ProgressDialog.class, RETURNS_DEEP_STUBS);

    @Spy @InjectMocks
    LoginTask loginTask = new LoginTask();

    @Before
    public void setUp() {
        doNothing().when(loginTask).createProgressDialog();
        doNothing().when(loginTask).dismissProgressDialog();
        doNothing().when(loginTask).notifyToast(anyInt());
        doNothing().when(loginTask).notifyToast(anyString());
        doReturn(true).when(connectivityBean).isOnline();
    }

    @Test
    public void testOnlineLogin() {
        User expected = mock(User.class);
        doReturn(expected).when(loginTask).loadOnline("foo", "bar", "baz");
        User actual = loginTask.login("foo", "bar", "baz");
        assertEquals(expected, actual);
        verify(loginTask).cacheForOffline(expected);
        verify(loginTask).loadFormSections();
    }

    @Test
    public void testOfflineLogin() {
        User expected = mock(User.class);
        doReturn(expected).when(loginTask).loadOffline("foo", "bar");
        User actual = loginTask.login("foo", "bar", "baz");
        assertEquals(expected, actual);
        verify(loginTask).cacheForOffline(expected);
        verify(loginTask, never()).loadFormSections();
    }

    @Test
    public void testOnlineUserWhenOfflineProvided() {
        User onlineUser = mock(User.class), offlineUser = mock(User.class);
        doReturn(onlineUser).when(loginTask).loadOnline("foo", "bar", "baz");
        doReturn(offlineUser).when(loginTask).loadOffline("foo", "bar");
        User actual = loginTask.login("foo", "bar", "baz");
        assertEquals(onlineUser, actual);
        verify(loginTask).cacheForOffline(onlineUser);
        verify(loginTask).loadFormSections();
    }

    @Test @Ignore
    public void testMigration() throws Exception {
        MigrateUnverifiedDataToVerified migrateTask = mock(MigrateUnverifiedDataToVerified.class, RETURNS_DEEP_STUBS);
        whenNew(MigrateUnverifiedDataToVerified.class).withAnyArguments().thenReturn(migrateTask);

        User onlineUser = mock(User.class), offlineUser = mock(User.class);
        doReturn(onlineUser).when(loginTask).loadOnline("foo", "bar", "baz");
        doReturn(offlineUser).when(loginTask).loadOffline("foo", "bar");
        doReturn(true).when(onlineUser).isVerified();
        doReturn(false).when(offlineUser).isVerified();

        loginTask.login("foo", "bar", "baz");
        verify(migrateTask).execute();
    }

    @Test
    public void testSaveDetailsOffline() throws IOException, GeneralSecurityException {
        User user = mock(User.class, RETURNS_DEEP_STUBS);
        loginTask.cacheForOffline(user);

        verify(user).save();
        verify(rapidFtrApplication).setCurrentUser(user);
    }

}
