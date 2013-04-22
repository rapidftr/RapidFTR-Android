package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.utils.http.FluentRequest;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class RegisterUserServiceTest {

    @Test
    public void shouldRegisterUnverifiedUser() throws Exception {
        RapidFtrApplication mockContext = RapidFtrApplication.getApplicationInstance();
        mockContext.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();

        FluentRequest mockFluentRequest = spy(new FluentRequest());
        getFakeHttpLayer().addHttpResponseRule("POST", "http://whatever/api/register", new TestHttpResponse(200, "{}"));

        new RegisterUserService(mockContext, mockFluentRequest).register(mock(User.class));
    }
}
