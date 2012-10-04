package com.rapidftr.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHandler;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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

    @Test
    public void shouldThrowConnectionRefusedIfServerIsNotAvailable() throws IOException {
        serverUrl.setText("http://rapidftr.com");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(404,"some response body");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.server_not_reachable)));
    }

    @Test
    public void shouldLoginSuccessfullyForValidUserAndUrl() {
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(201, "some response body");

        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.login_successful)));
    }

    @Test
    public void shouldSaveServerUrlAfterSuccessfulLogin(){
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("SERVER_URL", "").commit();

        serverUrl.setText("http://dev.rapidftr.com:3000");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(201, "some response body");

        loginButton.performClick();
        assertThat(sharedPreferences.getString("SERVER_URL", ""), equalTo(serverUrl.getText().toString()));
    }

    @Test
    public void shouldRestoreServerUrlOnLoad() {
        String url = "http://dev.rapidftr.com:3000";
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", Context.MODE_PRIVATE);
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

}
