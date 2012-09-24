package com.rapidftr.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import com.google.inject.Inject;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.http.*;
import com.rapidftr.service.LoginService;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHandler;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CustomTestRunner.class)
public class LoginActivityTest {

    private LoginActivity loginActivity;
    private Button loginButton;
    private EditText serverUrl;
    @Inject
    private LoginService loginService;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loginActivity = new LoginActivity();
        loginActivity.onCreate(null);
        loginButton = (Button) loginActivity.findViewById(R.id.login_button);
        serverUrl = (EditText) loginActivity.findViewById(R.id.base_url);
        loginService = mock(LoginService.class);
    }

    @Test
    public void shouldThrowUnauthorizedErrorForInvalidUsernameAndPassword() throws IOException {
        when(loginService.login(Robolectric.application, "", "", "http://dev.rapidftr.com:3000")).thenReturn(mock(HttpResponse.class));
        Robolectric.setDefaultHttpResponse(401, "Unauthroized");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.unauthorized)));
        verify(loginService);
    }

    @Test
    public void shouldThrowConnectionRefusedIfServerIsNotAvailable() {
        Robolectric.setDefaultHttpResponse(404, "Not Found");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.server_not_reachable)));
    }

  @Test
    public void shouldLoginSuccessfullyForValidUserAndUrl() {
        Robolectric.setDefaultHttpResponse(200,"OK");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.login_successful)));
    }
    @Test
    public void shouldSaveServerUrlAfterSuccessfulLogin(){
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("SERVER_URL", "http://dev.rapidftr.com:3000").commit();
        serverUrl.setText("http://dev.rapidftr.com:3000");
        Robolectric.setDefaultHttpResponse(200,"OK");
        loginButton.performClick();
        assertThat(loginActivity.getApplicationContext().getSharedPreferences("RAPIDFTR_PREFERENCES", 0).getString("SERVER_URL", ""), equalTo(serverUrl.getText().toString()));
    }

}
