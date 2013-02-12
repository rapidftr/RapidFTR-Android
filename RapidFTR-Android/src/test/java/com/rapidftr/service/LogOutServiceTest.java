package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.utils.http.FluentRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class LogOutServiceTest {

	protected LogOutService service;
	protected RapidFtrApplication application;
	protected RapidFtrActivity activity;

	@Before
	public void setUp() {
		service = spy(new LogOutService());
		application = spy(RapidFtrApplication.getApplicationInstance());
		activity = mock(RapidFtrActivity.class, RETURNS_DEEP_STUBS);
		given(activity.getContext()).willReturn(application);
	}

    @Test
    public void shouldUpdateContextOnLogout() throws IOException {
        application.setSyncTask(null);
        service.attemptLogOut(activity);
        verify(application).setCurrentUser(null);
    }

	@Test
	public void shouldClearCookiesOnLogout() {
		CookieStore spyCookieStore = spy(FluentRequest.getHttpClient().getCookieStore());
		FluentRequest.getHttpClient().setCookieStore(spyCookieStore);

		service.attemptLogOut(activity);
		verify(spyCookieStore).clear();
	}

}