package com.rapidftr.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.task.LoginAsyncTask;
import com.rapidftr.utils.SpyActivityController;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;
import static com.rapidftr.RapidFtrApplication.FORM_SECTIONS_PREF;
import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.robolectric.Robolectric.shadowOf;
import static org.robolectric.Robolectric.shadowOf_;

@RunWith(CustomTestRunner.class)
public class LoginActivityTest {

    private Button loginButton;
    private EditText serverUrl;
    private EditText userName;
    private EditText password;
    private TextView signUp;

    private ActivityController<LoginActivity> activityController;
    private LoginActivity loginActivity;

    @Before
    public void setUp() throws Exception {
        activityController = SpyActivityController.of(LoginActivity.class);
        loginActivity = activityController.attach().get();
	    loginActivity.getContext().setCurrentUser(null);
        loginActivity.getContext().setFormSections((String) null);
        activityController.create();

        loginButton = (Button) loginActivity.findViewById(R.id.login_button);
        signUp = (TextView) loginActivity.findViewById(R.id.new_user_signup_link);
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

    @Ignore
    @Test
    public void shouldThrowConnectionRefusedIfServerIsNotAvailable() throws IOException {
        serverUrl.setText("rapidftr.com:abcd");
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(404,"some response body");
        loginButton.performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(loginActivity.getString(R.string.server_not_reachable)));
    }

    @Test
    public void shouldRestoreServerUrlOnLoad() {
        String url = "http://dev.rapidftr.com:3000";
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("RAPIDFTR_PREFERENCES", MODE_PRIVATE);
        sharedPreferences.edit().putString("SERVER_URL", "http://dev.rapidftr.com:3000").commit();

        activityController.create();
        assertThat(serverUrl.getText().toString(), equalTo(url));
    }

    @Test
    public void shouldRequireUsernamePasswordAndServerURL() {
        userName  = mock(EditText.class);
        password  = mock(EditText.class);
        serverUrl = mock(EditText.class);

        doReturn(userName).when(loginActivity).findViewById(R.id.username);
        doReturn(password).when(loginActivity).findViewById(R.id.password);

        assertThat(loginActivity.isValid(), equalTo(false));
        verify(userName).setError(loginActivity.getString(R.string.username_required));
        verify(password).setError(loginActivity.getString(R.string.password_required));
    }

    @Test
    public void shouldReturnEmptyStringWhenEditTextIsEmpty() {
        userName.setText("     ");
        assertThat(loginActivity.getEditText(R.id.username), equalTo(""));
    }

    @Test
    public void shouldStartSignUpActivityWhenClickedOnSignUpLink(){
        signUp.performClick();
        ShadowActivity shadowActivity = shadowOf_(new LoginActivity());
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf_(startedIntent);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo("com.rapidftr.activity.SignupActivity"));
    }

}
