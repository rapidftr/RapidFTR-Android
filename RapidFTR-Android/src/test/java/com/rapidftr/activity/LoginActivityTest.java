package com.rapidftr.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.runner.ActivityTestInjector;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHandler;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class LoginActivityTest {

    private Button loginButton;
    private EditText serverUrl;
    private EditText userName;
    private EditText password;

    @Inject
    private LoginActivity loginActivity;

    private final ActivityTestInjector<LoginActivity> activityTestInjector =
            new ActivityTestInjector<LoginActivity>(this, LoginActivity.class);


    @Before
    public void setUp() throws Exception {
        activityTestInjector.configureActivity();
        loginActivity.onCreate(null);
        loginButton = (Button) loginActivity.findViewById(R.id.login_button);
        serverUrl = (EditText) loginActivity.findViewById(R.id.base_url);
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
        serverUrl.setText("invalid url");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(404,"some response body");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.server_not_reachable)));
    }

    @Test
    public void shouldLoginSuccessfullyForValidUserAndUrl() {
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(200,"some response body");

        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.login_successful)));
    }
    @Test
    public void shouldSaveServerUrlAfterSuccessfulLogin(){
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("SERVER_URL", "http://dev.rapidftr.com:3000").commit();
        serverUrl.setText("http://dev.rapidftr.com:3000");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(200, "some response body");
        loginButton.performClick();
        assertThat(loginActivity.getApplicationContext().getSharedPreferences("RAPIDFTR_PREFERENCES", 0).getString("SERVER_URL", ""), equalTo(serverUrl.getText().toString()));
    }

}
