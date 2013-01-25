package com.rapidftr.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowIntent;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.CustomTestRunner.createUser;
import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class SignupActivityTest {
    private EditText serverUrl;
    private EditText userName;
    private EditText password;
    private EditText confirmPassword;
    private EditText organisation;
    private SignupActivity signupActivity;

    @Before
    public void setup(){
        signupActivity = new SignupActivity();
        signupActivity.onCreate(null);
        signupActivity = spy(signupActivity);
        userName = (EditText)signupActivity.findViewById(R.id.username);
        password = (EditText)signupActivity.findViewById(R.id.password);
        confirmPassword = (EditText)signupActivity.findViewById(R.id.confirm_password);
        organisation = (EditText)signupActivity.findViewById(R.id.organisation);
    }

    @Test
    public void shouldCheckIfMandatoryFieldsAreFilled()
    {
        userName  = mock(EditText.class);
        password  = mock(EditText.class);
        organisation = mock(EditText.class);
        confirmPassword = mock(EditText.class);

        doReturn(userName).when(signupActivity).findViewById(R.id.username);
        doReturn(password).when(signupActivity).findViewById(R.id.password);
        doReturn(organisation).when(signupActivity).findViewById(R.id.organisation);
        doReturn(confirmPassword).when(signupActivity).findViewById(R.id.confirm_password);

        assertThat(signupActivity.isValid(), equalTo(false));
        verify(userName).setError(signupActivity.getString(R.string.username_required));
        verify(password).setError(signupActivity.getString(R.string.password_required));
        verify(organisation).setError(signupActivity.getString(R.string.organisation_required));
        verify(confirmPassword).setError(signupActivity.getString(R.string.confirm_password_required));
    }

    @Test
    public void shouldSetErrorMessageIfConfirmPasswordIsNotSameAsPassword(){
        userName  = mock(EditText.class);
        password  = mock(EditText.class);
        organisation = mock(EditText.class);
        confirmPassword = mock(EditText.class);

        password.setText("text");
        confirmPassword.setText("randomText");
        userName.setText("user");
        organisation.setText("org");

        doReturn(userName).when(signupActivity).findViewById(R.id.username);
        doReturn(confirmPassword).when(signupActivity).findViewById(R.id.confirm_password);
        doReturn(organisation).when(signupActivity).findViewById(R.id.organisation);
        doReturn("text").when(signupActivity).getEditText(R.id.password);
        doReturn("confirmText").when(signupActivity).getEditText(R.id.confirm_password);
        doReturn(true).when(signupActivity).validatesPresenceOfMandatoryFields();

        assertThat(signupActivity.isValid(), equalTo(false));
        verify(confirmPassword).setError(signupActivity.getString(R.string.password_mismatch));
    }

    @Test
    public void shouldSaveUserDetailsInSharedPreferences() throws Exception {
	    User user = spy(createUser());
	    doNothing().when(user).save();
	    doReturn(user).when(signupActivity).buildUser();
	    doReturn(true).when(signupActivity).isValid();
        signupActivity.createUser(null);
	    verify(user).save();
    }

    @Test
    public void shouldStartLoginActivityAfterSignup() throws Exception {
        doReturn("username").when(signupActivity).getEditText(R.id.username);
        doReturn("fullname").when(signupActivity).getEditText(R.id.full_name);
        doReturn("organisation").when(signupActivity).getEditText(R.id.organisation);
        doReturn("text").when(signupActivity).getEditText(R.id.password);
        doReturn("text").when(signupActivity).getEditText(R.id.confirm_password);

        signupActivity.createUser(null);
        ShadowActivity signupActivity = shadowOf(new SignupActivity());
        Intent startedIntent = signupActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);

        assertThat(shadowIntent.getComponent().getClassName(), equalTo("com.rapidftr.activity.LoginActivity"));
    }

    @Test
    public void shouldShowToastAfterRedirectedToLoginPage() throws Exception {
        doReturn("username").when(signupActivity).getEditText(R.id.username);
        doReturn("fullname").when(signupActivity).getEditText(R.id.full_name);
        doReturn("organisation").when(signupActivity).getEditText(R.id.organisation);
        doReturn("text").when(signupActivity).getEditText(R.id.password);
        doReturn("text").when(signupActivity).getEditText(R.id.confirm_password);

        signupActivity.createUser(null);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(signupActivity.getString(R.string.registered)+" username"));
    }

    @Test
    public void shouldCheckIfUsernameIsAlreadyTakenInMobile() throws Exception {
        signupActivity.getContext().getSharedPreferences().edit().putString("user_username","{}").commit();
        userName  = mock(EditText.class);

        doReturn(userName).when(signupActivity).findViewById(R.id.username);
        doReturn("username").when(signupActivity).getEditText(R.id.username);
        doReturn("fullname").when(signupActivity).getEditText(R.id.full_name);
        doReturn("organisation").when(signupActivity).getEditText(R.id.organisation);
        doReturn("text").when(signupActivity).getEditText(R.id.password);
        doReturn("text").when(signupActivity).getEditText(R.id.confirm_password);

        signupActivity.createUser(null);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(signupActivity.getString(R.string.username_taken)));
        verify(userName).setError(signupActivity.getString(R.string.username_taken));
    }
}
