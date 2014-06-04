package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.LoginService;
import com.rapidftr.service.RegisterUserService;
import com.rapidftr.utils.http.FluentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.tester.org.apache.http.TestHttpResponse;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class RegisterUnverifiedUserAsyncTaskTest {

    @Mock private RegisterUserService registerUserService;
    @Mock private User user;
    @Mock private RapidFtrApplication context;
    @Mock private LoginService loginService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldCallRegisterUserServiceToRegisterUser() throws IOException {
        RegisterUnverifiedUserAsyncTask registerUserTask = new RegisterUnverifiedUserAsyncTask(registerUserService, user, context);

        given(registerUserService.register(user)).willReturn(new FluentResponse(new TestHttpResponse(201, "created")));

        assertThat(registerUserTask.doInBackground("url"), is(true));
    }
}
