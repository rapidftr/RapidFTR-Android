package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.http.FluentRequest;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class ChangePasswordServiceTest {

    @Test
    public void shouldRegisterUnverifiedUser() throws Exception {
        RapidFtrApplication mockContext = RapidFtrApplication.getApplicationInstance();
        mockContext.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();

        FluentRequest mockFluentRequest = spy(new FluentRequest());
        getFakeHttpLayer().addHttpResponseRule("POST", "http://whatever/users/update_password", new TestHttpResponse(200, "{}"));

        new ChangePasswordService(mockContext, mockFluentRequest).updatePassword("password1", "password2", "password2");
    }

}
