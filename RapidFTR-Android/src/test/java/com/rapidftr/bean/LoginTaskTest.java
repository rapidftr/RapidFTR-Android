package com.rapidftr.bean;

import android.app.ProgressDialog;
import android.content.Context;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.LoginService;
import com.rapidftr.task.MigrateUnverifiedDataToVerified;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@MockPolicy(AndroidMockPolicy.class)
@PrepareForTest({ LoginService.class, MigrateUnverifiedDataToVerified.class })
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
        doNothing().when(loginTask).notifyToast(any(LoginTask.LoginException.class));
        doNothing().when(loginTask).notifyToast(anyInt());
        doReturn(true).when(connectivityBean).isOnline();
    }

    @Test
    public void testOnlineLogin() {
        doReturn(true).when(loginTask).loginOnline("test1", "test2", "test3");
        loginTask.login("test1", "test2", "test3");
        verify(loginTask, never()).loginOffline(anyString(), anyString());
    }

    @Test
    public void testOfflineLogin() {
        doReturn(false).when(loginTask).loginOnline("test1", "test2", "test3");
        doReturn(false).when(loginTask).loginOffline("test1", "test2");
        loginTask.login("test1", "test2", "test3");
        verify(loginTask).loginOffline("test1", "test2");
    }

    @Test
    public void testLoginOnlineShouldFailIfLoadFails() {
        LoginTask.LoginException loginException = new LoginTask.LoginException(1, null);
        doThrow(loginException).when(loginTask).loadOnline("test1", "test2", "test3");

        boolean result = loginTask.loginOnline("test1", "test2", "test3");
        assertFalse(result);
        verify(loginTask, never()).migrateIfVerified(any(User.class));
        verify(loginTask, never()).cacheForOffline(any(User.class));
        verify(loginTask, never()).loadFormSections();
        verify(loginTask).notifyToast(loginException);
    }

    @Test
    public void testLoginOnlineShouldFailIfMigrationFails() {
        LoginTask.LoginException loginException = new LoginTask.LoginException(1, null);
        User user = mock(User.class);
        doReturn(user).when(loginTask).loadOnline("test1", "test2", "test3");
        doThrow(loginException).when(loginTask).migrateIfVerified(user);

        boolean result = loginTask.loginOnline("test1", "test2", "test3");
        assertFalse(result);
        verify(loginTask, never()).cacheForOffline(any(User.class));
        verify(loginTask).notifyToast(loginException);
    }

    @Test
    public void testLoginOnlineShouldFailIfCachingFails() {
        LoginTask.LoginException loginException = new LoginTask.LoginException(1, null);
        User user = mock(User.class);
        doReturn(user).when(loginTask).loadOnline("test1", "test2", "test3");
        doNothing().when(loginTask).migrateIfVerified(user);
        doThrow(loginException).when(loginTask).cacheForOffline(user);

        boolean result = loginTask.loginOnline("test1", "test2", "test3");
        assertFalse(result);
        verify(loginTask).notifyToast(loginException);
    }

    @Test
    public void testLoginOnlineShouldFailIfFormSectionsFail() {
        LoginTask.LoginException loginException = new LoginTask.LoginException(1, null);
        User user = mock(User.class);
        doReturn(user).when(loginTask).loadOnline("test1", "test2", "test3");
        doNothing().when(loginTask).migrateIfVerified(user);
        doNothing().when(loginTask).cacheForOffline(user);
        doThrow(loginException).when(loginTask).loadFormSections();

        boolean result = loginTask.loginOnline("test1", "test2", "test3");
        assertFalse(result);
        verify(loginTask).notifyToast(loginException);
    }

    @Test
    public void testLoginOnlineShouldBeSuccess() {
        User user = mock(User.class);
        doReturn(user).when(loginTask).loadOnline("test1", "test2", "test3");
        doNothing().when(loginTask).migrateIfVerified(user);
        doNothing().when(loginTask).cacheForOffline(user);
        doNothing().when(loginTask).loadFormSections();

        boolean result = loginTask.loginOnline("test1", "test2", "test3");
        assertTrue(result);
        verify(loginTask).notifyToast(R.string.login_online_success);
    }

    @Test
    public void testLoginOfflineShouldFailIfLoadingFromOfflineFails() {
        LoginTask.LoginException loginException = new LoginTask.LoginException(1, null);
        doThrow(loginException).when(loginTask).loadOffline("test1", "test2");

        boolean result = loginTask.loginOffline("test1", "test2");
        assertFalse(result);
        verify(loginTask, never()).cacheForOffline(any(User.class));
        verify(loginTask).notifyToast(loginException);
    }

    @Test
    public void testLoginOfflineShouldFailIfCachingForOfflineFails() {
        LoginTask.LoginException loginException = new LoginTask.LoginException(1, null);
        User user = mock(User.class);
        doReturn(user).when(loginTask).loadOffline("test1", "test2");
        doThrow(loginException).when(loginTask).cacheForOffline(user);

        boolean result = loginTask.loginOffline("test1", "test2");
        assertFalse(result);
        verify(loginTask).notifyToast(loginException);
    }

    @Test
    public void testLoginOfflineShouldSucceed() {
        User user = mock(User.class);
        doReturn(user).when(loginTask).loadOffline("test1", "test2");
        doNothing().when(loginTask).cacheForOffline(user);

        boolean result = loginTask.loginOffline("test1", "test2");
        assertTrue(result);
        verify(loginTask).notifyToast(R.string.login_offline_success);
    }

    @Test
    public void testMigrateShouldNotMigrate() throws Exception {
        whenNew(MigrateUnverifiedDataToVerified.class).withAnyArguments().thenReturn(null);
        User user = new User("test1", "test2", false);
        doThrow(LoginTask.LoginException.class).when(loginTask).loadOffline("test1", "test2");

        loginTask.migrateIfVerified(user);
        verifyNew(MigrateUnverifiedDataToVerified.class, never());
    }

    @Test @Ignore
    public void testMigrateShouldMigrate() throws Exception {
        User onlineUser = new User("test1", "test2", false);
        User offlineUser = mock(User.class);

        MigrateUnverifiedDataToVerified task = mock(MigrateUnverifiedDataToVerified.class, RETURNS_DEEP_STUBS);
        whenNew(MigrateUnverifiedDataToVerified.class).withArguments(onlineUser.asJSON(), offlineUser).thenReturn(task);

        loginTask.migrateIfVerified(onlineUser);
        verify(task).execute();
    }

}
