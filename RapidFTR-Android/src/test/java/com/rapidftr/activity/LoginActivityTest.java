package com.rapidftr.activity;


import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.EncryptionUtil;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHandler;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;
import static com.rapidftr.RapidFtrApplication.Preference.FORM_SECTION;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class LoginActivityTest {

    private Button loginButton;
    private EditText serverUrl;
    private EditText userName;
    private EditText password;

    private LoginActivity loginActivity;

    @Before
    public void setUp() throws Exception {
        loginActivity = new LoginActivity();
        loginActivity.getContext().setPreference(USER_NAME, null);
        loginActivity.getContext().setPreference(USER_ORG, null);
        loginActivity.getContext().setDbKey(null);
        loginActivity.getContext().setFormSections(null);
        loginActivity.onCreate(null);

        loginButton = (Button) loginActivity.findViewById(R.id.login_button);
        serverUrl = (EditText) loginActivity.findViewById(R.id.url);
        serverUrl.setText("http://dev.rapidftr.com:3000");
        userName = (EditText) loginActivity.findViewById(R.id.username);
        userName.setText("rapidftr");
        password = (EditText) loginActivity.findViewById(R.id.password);
        password.setText("rapidftr");
    }

    @Test
    public void shouldThrowUnauthorizedErrorForInvalidUsernameAndPassword() throws IOException {
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(401,"some response body");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.unauthorized)));
    }

    @Ignore
    @Test
    public void shouldThrowConnectionRefusedIfServerIsNotAvailable() throws IOException {
        serverUrl.setText("rapidftr.com:abcd");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(404,"some response body");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.server_not_reachable)));
    }

    @Test
    public void shouldTimeoutLongRunningOperations() throws IOException {
        // TODO: A Test must be written to ensure long running operations are timed out
        // Currently this is work in progress, we are evaluating how to go about testing this
    }

    @Test
    public void shouldLoginSuccessfullyForValidUserAndUrl() {
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", MODE_PRIVATE);
        sharedPreferences.edit().putString(FORM_SECTION.getKey(), "some form section").commit();
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(201, "some response body");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.login_successful)));
    }

    @Test
    public void shouldSaveServerUrlAfterSuccessfulLogin(){
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", MODE_PRIVATE);
        sharedPreferences.edit().putString("SERVER_URL", "").commit();
        sharedPreferences.edit().putString(FORM_SECTION.getKey(), "form_section").commit();

        serverUrl.setText("http://dev.rapidftr.com:3000");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(201, "some response body");

        loginButton.performClick();
        assertThat(sharedPreferences.getString("SERVER_URL", ""), equalTo(serverUrl.getText().toString()));
    }

    @Test
    public void shouldRestoreServerUrlOnLoad() {
        String url = "http://dev.rapidftr.com:3000";
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", MODE_PRIVATE);
        sharedPreferences.edit().putString("SERVER_URL", "http://dev.rapidftr.com:3000").commit();

        loginActivity.onCreate(null);
        assertThat(serverUrl.getText().toString(), equalTo(url));
    }

    @Test
    public void shouldRequireUsernamePasswordAndServerURL() {
        userName  = mock(EditText.class);
        password  = mock(EditText.class);
        serverUrl = mock(EditText.class);

        loginActivity = spy(loginActivity);
        doReturn(userName).when(loginActivity).findViewById(R.id.username);
        doReturn(password).when(loginActivity).findViewById(R.id.password);
        doReturn(serverUrl).when(loginActivity).findViewById(R.id.url);

        assertThat(loginActivity.isValid(), equalTo(false));
        verify(userName).setError(loginActivity.getString(R.string.username_required));
        verify(password).setError(loginActivity.getString(R.string.password_required));
        verify(serverUrl).setError(loginActivity.getString(R.string.url_required));
    }

    @Test
    public void shouldReturnEmptyStringWhenEditTextIsEmpty() {
        userName.setText("     ");
        assertThat(loginActivity.getEditText(R.id.username), equalTo(""));
    }

    @Test
    public void shouldNotStoreTheDbKeyWhenFailingToLogin() {
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(404, "{'db_key' : 'fa8f5e7599ed5402'}");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(loginActivity.getContext().getDbKey(), is(nullValue()));
    }

    @Test
    public void shouldStoreTheDbKeyInMemoryAfterSuccessfulLogin() {
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(201, "{'db_key' : 'fa8f5e7599ed5402'}");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(loginActivity.getContext().getDbKey(), is("fa8f5e7599ed5402"));
    }

    @Test
    public void shouldStoreEncryptedDbKeyAfterSuccessfulLogin() throws Exception {
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(201, "{'db_key' : 'fa8f5e7599ed5402'}");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        SharedPreferences sharedPreferences = RapidFtrApplication.getApplicationInstance().getSharedPreferences();
        String encryptedDBKey = sharedPreferences.getString(userName.getText().toString(), null);
        assertThat("fa8f5e7599ed5402", is(EncryptionUtil.decrypt(password.getText().toString(), encryptedDBKey)));
    }

    @Test
    public void shouldGotoHomeScreenOnSuccessfulOfflineLogin() {
        LoginActivity.LoginAsyncTask loginAsyncTask = offlineLogin(true);
        loginAsyncTask.onPostExecute(null);

        verify(loginActivity).goToHomeScreen();
    }

    @Test
    public void shouldReturnTrueIfGivenPasswordIsCorrectForOfflineLogin() throws Exception {
        LoginActivity.LoginAsyncTask loginAsyncTask = setUpForOffLineLogin();
        assertTrue(loginAsyncTask.processOfflineLogin("user_name", "password"));
    }

    @Test
    public void shouldReturnFalseIfGivenPasswordIsInCorrectForOfflineLogin() throws Exception {
        LoginActivity.LoginAsyncTask loginAsyncTask = setUpForOffLineLogin();
        assertFalse(loginAsyncTask.processOfflineLogin("user_name", "wrongpassword"));
    }

    @Test
    public void shouldShowAptToastMessageForUnsuccesfulOfflineLogin() {
        LoginActivity.LoginAsyncTask loginAsyncTask = offlineLogin(false);
        loginAsyncTask.onPostExecute(null);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.unauthorized)));
    }

    @Test
    public void shouldShowAptToastMessageForSuccesfulOfflineLogin() {
        LoginActivity.LoginAsyncTask loginAsyncTask = offlineLogin(true);
        loginAsyncTask.onPostExecute(null);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.login_successful)));
    }

    @Test
    public void shouldSetLoginDbkeyAndFormSectionOnSuccessfulOfflineLogin() throws Exception {
        String dbKey = "db_key_from_server";
        String formSectionTemplate = "form section template";
        String encryptedDBKey = EncryptionUtil.encrypt("password", dbKey);
        LoginActivity spyLoginActivity = spy(loginActivity);
        RapidFtrApplication application = mock(RapidFtrApplication.class);
        doReturn(encryptedDBKey).when(application).getPreference(anyString());
        doReturn(application).when(spyLoginActivity).getContext();
        doReturn(formSectionTemplate).when(application).getPreference(FORM_SECTION);
        LoginActivity.LoginAsyncTask loginAsyncTask = spy(spyLoginActivity.getLoginAsyncTask());
        doReturn(dbKey).when(loginAsyncTask).decryptedDBKey(anyString(), anyString());
        loginAsyncTask.onPreExecute();
        loginAsyncTask.onPostExecute(null);

        verify(application).setDbKey(dbKey);
        verify(application).setLoggedIn(true);
        verify(application).setFormSectionsTemplate(formSectionTemplate);
    }

    private LoginActivity.LoginAsyncTask offlineLogin(boolean loginStatus) {
        loginActivity = spy(loginActivity);
        RapidFtrApplication mockContext = mock(RapidFtrApplication.class);
        doReturn(mockContext).when(loginActivity).getContext();
        doReturn("form_section").when(mockContext).getPreference(FORM_SECTION);
        LoginActivity.LoginAsyncTask loginAsyncTask = loginActivity.getLoginAsyncTask();
        loginAsyncTask.onPreExecute();
        loginAsyncTask = spy(loginAsyncTask);
        doReturn(loginStatus).when(loginAsyncTask).processOfflineLogin(anyString(), anyString());
        return loginAsyncTask;
    }

    private LoginActivity.LoginAsyncTask setUpForOffLineLogin() throws Exception {
        String encryptedDBKey = EncryptionUtil.encrypt("password", "db_key_from_server");
        LoginActivity spyLoginActivity = spy(loginActivity);
        RapidFtrApplication application = mock(RapidFtrApplication.class);
        doReturn(encryptedDBKey).when(application).getPreference(anyString());
        doReturn(application).when(spyLoginActivity).getContext();
        return spyLoginActivity.getLoginAsyncTask();
    }
}
