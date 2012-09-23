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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class LoginActivityTest {

    private LoginActivity loginActivity;
    private Button loginButton;
    private EditText serverUrl;

    @Before
    public void setUp() throws Exception {
        loginActivity = new LoginActivity();
        loginActivity.onCreate(null);
        loginButton = (Button) loginActivity.findViewById(R.id.login_button);
        serverUrl = (EditText) loginActivity.findViewById(R.id.base_url);
    }

    @Test
    public void shouldThrowUnauthorizedErrorForInvalidUsernameAndPassword() {
        Robolectric.setDefaultHttpResponse(401, "Unauthroized");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.unauthorized)));
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
