package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.User;
import com.rapidftr.service.FormService;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.rapidftr.CustomTestRunner.createUser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class LoginAsyncTaskTest {

    @Mock private RapidFtrActivity activity;
    @Mock private FormService formService;

    private RapidFtrApplication application;
    private LoginAsyncTask loginAsyncTask;

    @Before
    public void setUp() {
        initMocks(this);
        application = spy(RapidFtrApplication.getApplicationInstance());
        loginAsyncTask = spy(new LoginAsyncTask(application, formService));
        loginAsyncTask.setActivity(activity);
    }

	@Test
	public void shouldCallOnlineLoginIfNetworkIsUp() throws IOException, JSONException, GeneralSecurityException {
		doReturn(true).when(application).isOnline();
		loginAsyncTask.doInBackground("", "", "");
		verify(loginAsyncTask).doOnlineLogin();
	}

	@Test
	public void shouldCallOfflineLoginIfNetworkIsDown() throws GeneralSecurityException, IOException {
		doReturn(false).when(application).isOnline();
		loginAsyncTask.doInBackground("", "", "");
		verify(loginAsyncTask).doOfflineLogin();
	}

    @Test
    public void shouldSaveCurrentUserWhenSuccess() throws IOException, GeneralSecurityException {
	    User user = spy(createUser());
	    doReturn("{}").when(user).asJSON();
        loginAsyncTask.onPostExecute(user);

	    verify(user).save();
	    verify(application).setCurrentUser(user);
	    verify(loginAsyncTask).goToHomeScreen();
    }

    @Test
    public void shouldCheckOfflineLogin() throws Exception {
	    User expectedUser = createUser();
	    expectedUser.setVerified(false);
	    expectedUser.save();

	    loginAsyncTask.userName = expectedUser.getUserName();
	    loginAsyncTask.password = expectedUser.getPassword();
	    User actualUser = loginAsyncTask.doOfflineLogin();
	    assertThat(actualUser, equalTo(expectedUser));
    }

    @Test(expected = GeneralSecurityException.class)
    public void shouldReturnFalseIfGivenPasswordIsInCorrectForOfflineLogin() throws Exception {
	    User user = createUser();
	    user.setVerified(false);
	    user.save();

	    loginAsyncTask.userName = user.getUserName();
	    loginAsyncTask.password = "asdf";
	    loginAsyncTask.doOfflineLogin();
    }

    @Test
    public void shouldShowAptToastMessageForUnsuccesfulOfflineLogin() {
        loginAsyncTask.onPostExecute(null);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.unauthorized)));
    }

    @Test
    public void shouldShowAptToastMessageForSuccesfulOfflineLogin() throws IOException, GeneralSecurityException {
        loginAsyncTask.onPostExecute(createUser());
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.login_successful)));
    }

    @Test
    public void shouldDownloadFormSectionsUponSuccessfulLogin() {
        User user = createUser();
        doNothing().when(loginAsyncTask).getFormSections(user);
        loginAsyncTask.onPostExecute(user);
        verify(loginAsyncTask).getFormSections(user);
    }

    @Test
    public void shouldDownloadFormSections() throws IOException {
        User user = createUser();
        doReturn(true).when(application).isOnline();
        loginAsyncTask.getFormSections(user);
        verify(formService).getPublishedFormSections();
    }

    @Test
    public void shouldNotDownloadFormSectionsDuringOfflineLogin() throws IOException {
        User user = createUser();
        doReturn(false).when(application).isOnline();
        loginAsyncTask.getFormSections(user);
        verifyZeroInteractions(formService);
    }

    @Test
    public void shouldNotDownloadFormSectionsForUnverifiedUser() throws IOException {
        User user = createUser();
        user.setVerified(false);
        doReturn(true).when(application).isOnline();
        loginAsyncTask.getFormSections(user);
        verifyZeroInteractions(formService);
    }

    @Test
    public void shouldCallMigrateUnverifiedDataTaskIfLoggedInUserIsVerifiedAndHisStausInSharedPrefIsUnVerified() throws IOException, GeneralSecurityException, JSONException {
        User userFromSharedPreference = mock(User.class);
        HttpResponse loginResponse = new TestHttpResponse(201, "{\"verified\":true, \"db_key\":\"hey_from_server\", \"organisation\":\"tw\",\"language\":\"en\"}");
        doReturn(userFromSharedPreference).when(loginAsyncTask).getUserFromPreference();
        doReturn(loginResponse).when(loginAsyncTask).getLoginResponse();
        doNothing().when(loginAsyncTask).migrateUnverifiedData(anyString(), eq(userFromSharedPreference));
        loginAsyncTask.doOnlineLogin();
        verify(loginAsyncTask).migrateUnverifiedData("{\"verified\":true, \"db_key\":\"hey_from_server\", \"organisation\":\"tw\",\"language\":\"en\"}", userFromSharedPreference);
    }

}
