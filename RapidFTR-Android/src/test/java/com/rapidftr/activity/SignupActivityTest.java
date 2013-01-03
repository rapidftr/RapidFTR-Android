package com.rapidftr.activity;

import android.widget.EditText;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
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

        signupActivity = spy(signupActivity);
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

        signupActivity = spy(signupActivity);

        doReturn(userName).when(signupActivity).findViewById(R.id.username);
        doReturn(confirmPassword).when(signupActivity).findViewById(R.id.confirm_password);
        doReturn(organisation).when(signupActivity).findViewById(R.id.organisation);
        doReturn("text").when(signupActivity).getEditText(R.id.password);
        doReturn("confirmText").when(signupActivity).getEditText(R.id.confirm_password);
        doReturn(true).when(signupActivity).validatesPresenceOfMandatoryFields();

        assertThat(signupActivity.isValid(), equalTo(false));
        verify(confirmPassword).setError(signupActivity.getString(R.string.password_mismatch));
    }
}
