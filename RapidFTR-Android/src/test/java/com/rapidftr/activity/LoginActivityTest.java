package com.rapidftr.activity;


import android.widget.EditText;
import android.widget.TextView;
import com.rapidftr.bean.AndroidMockPolicy;
import com.rapidftr.bean.LoginTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@MockPolicy(AndroidMockPolicy.class)
public class LoginActivityTest {

    EditText userNameView = mock(EditText.class, RETURNS_DEEP_STUBS);
    EditText passwordView = mock(EditText.class, RETURNS_DEEP_STUBS);
    EditText urlView = mock(EditText.class, RETURNS_DEEP_STUBS);
    TextView changeUrlView = mock(TextView.class, RETURNS_DEEP_STUBS);
    LoginTask loginTask = mock(LoginTask.class, RETURNS_DEEP_STUBS);

    LoginActivity loginActivity = mock(LoginActivity.class, RETURNS_DEEP_STUBS);

    @Before
    public void setUp() throws Exception {
        loginActivity.userNameView = userNameView;
        loginActivity.passwordView = passwordView;
        loginActivity.urlView = urlView;
        loginActivity.changeUrlView = changeUrlView;
        loginActivity.loginTask = loginTask;
    }

    @Test
    public void testGoToHomeScreenIfLoggedIn() {
        doCallRealMethod().when(loginActivity).afterCreate();
        loginActivity.afterCreate();
        verify(loginActivity).goToHomeScreenIfLoggedIn();
    }

    @Test
    public void shouldRestoreServerUrlWhenLoading() {
        doCallRealMethod().when(loginActivity).toggleBaseUrl();
        when(loginActivity.getContext().getSharedPreferences().getString(SERVER_URL_PREF, null)).thenReturn("rapidftr.com:1234");
        loginActivity.toggleBaseUrl();
        verify(loginActivity.urlView).setText("rapidftr.com:1234");
    }

    @Test
    public void shouldInvalidateUserNameAndPassword() {
        doCallRealMethod().when(loginActivity).isValid();
        given(loginActivity.userNameView.getText().toString()).willReturn(null);
        given(loginActivity.passwordView.getText().toString()).willReturn("");
        assertFalse(loginActivity.isValid());
    }

    @Test
    public void shouldValidateUserNameAndPassword() {
        doCallRealMethod().when(loginActivity).isValid();
        given(loginActivity.userNameView.getText().toString()).willReturn("test");
        given(loginActivity.passwordView.getText().toString()).willReturn("test");
        assertTrue(loginActivity.isValid());
    }

}
