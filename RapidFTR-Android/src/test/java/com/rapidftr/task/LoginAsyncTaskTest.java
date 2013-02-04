package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.User;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.rapidftr.CustomTestRunner.createUser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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
	public void shouldCallOnlineLoginIfNetworkIsUp() throws IOException, JSONException, GeneralSecurityException {
		doReturn(true).when(loginAsyncTask).isOnline();
		loginAsyncTask.doInBackground("", "", "");
		verify(loginAsyncTask).doOnlineLogin();
	}

	@Test
	public void shouldCallOfflineLoginIfNetworkIsDown() throws GeneralSecurityException, IOException {
		doReturn(false).when(loginAsyncTask).isOnline();
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

}
