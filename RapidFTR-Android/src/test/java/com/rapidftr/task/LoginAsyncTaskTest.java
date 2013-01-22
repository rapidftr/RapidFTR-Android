package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.LoginActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.User;
import com.rapidftr.utils.EncryptionUtil;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.Preference.FORM_SECTION;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class LoginAsyncTaskTest {

    private RapidFtrActivity activity;
    private RapidFtrApplication application;
    private LoginAsyncTask loginAsyncTask;

    @Before
    public void setUp() {
        activity = mock(RapidFtrActivity.class);
        application = spy(RapidFtrApplication.getApplicationInstance());
        when(activity.getContext()).thenReturn(application);

        loginAsyncTask = spy(new LoginAsyncTask(activity));
    }

    @Test
    public void shouldGotoHomeScreenOnSuccessfulOfflineLogin() {
        offlineLogin(true);
        loginAsyncTask.onPostExecute(null);
        verify(loginAsyncTask).goToHomeScreen();
    }

    @Test
    public void shouldReturnTrueIfGivenPasswordIsCorrectForOfflineLogin() throws Exception {
        setUpForOffLineLogin();
        assertTrue(loginAsyncTask.processOfflineLogin("user_name", "password"));
    }

    @Test
    public void shouldReturnFalseIfGivenPasswordIsInCorrectForOfflineLogin() throws Exception {
        setUpForOffLineLogin();
        assertFalse(loginAsyncTask.processOfflineLogin("user_name", "wrongpassword"));
    }

    @Test
    public void shouldShowAptToastMessageForUnsuccesfulOfflineLogin() {
        offlineLogin(false);
        loginAsyncTask.onPostExecute(null);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.unauthorized)));
    }

    @Test
    public void shouldShowAptToastMessageForSuccesfulOfflineLogin() {
        offlineLogin(true);
        loginAsyncTask.onPostExecute(null);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.login_successful)));
    }

    @Test
    public void shouldSetLoginDbkeyAndFormSectionOnSuccessfulOfflineLoginForAuthenticatedUsers() throws Exception {
        String dbKey = "db_key_from_server";
        String formSectionTemplate = "form section template";
        String encryptedDBKey = EncryptionUtil.encrypt("password", dbKey);
        User user = new User(true, encryptedDBKey, "org");

        doReturn(user.toString()).when(application).getPreference(anyString());
        doReturn(formSectionTemplate).when(application).getPreference(FORM_SECTION);
        doReturn(dbKey).when(loginAsyncTask).decryptDbKey(anyString(), anyString());

        loginAsyncTask.onPreExecute();
        loginAsyncTask.onPostExecute(null);

        verify(application).setDbKey(dbKey);
        verify(application).setLoggedIn(true);
        verify(application).setFormSectionsTemplate(formSectionTemplate);
    }

    @Test
    public void shouldSetDbKeyUserOrgAndUserNameForUnauthenticatedUsers() throws Exception {
        User user = new User(false, "org", "fullname", "password");
        doReturn(user.toString()).when(application).getPreference("username");
        loginAsyncTask.processOfflineLogin("username","password");

        verify(application).setDbKey(User.UNAUTHENTICATED_DB_KEY);
        verify(application).setPreference(USER_NAME, "username");
        verify(application).setPreference(USER_ORG, "org");
    }

    private void offlineLogin(boolean loginStatus) {
        doReturn("form_section").when(application).getPreference(FORM_SECTION);
        doReturn(loginStatus).when(loginAsyncTask).processOfflineLogin(anyString(), anyString());
        loginAsyncTask.onPreExecute();
    }

    private void setUpForOffLineLogin() throws Exception {
        String encryptedDBKey = EncryptionUtil.encrypt("password", "db_key_from_server");
        User user = new User(true, encryptedDBKey, "org");
        doReturn(user.toString()).when(application).getPreference(anyString());
    }


}
