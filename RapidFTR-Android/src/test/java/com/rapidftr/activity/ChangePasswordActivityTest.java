package com.rapidftr.activity;

import android.widget.Button;
import android.widget.EditText;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.utils.SpyActivityController;
import com.rapidftr.utils.TestInjectionModule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ChangePasswordActivityTest {

    private EditText currentPassword;
    private EditText newPassword;
    private EditText newPasswordConfirm;
    private Button changeButton;
    private ChangePasswordActivity changePasswordActivity;


    @Before
    public void setUp() throws Exception {
        TestInjectionModule module = new TestInjectionModule();
        module.addBinding(DatabaseHelper.class, ShadowSQLiteHelper.getInstance());
        TestInjectionModule.setUp(this, module);
        changePasswordActivity = SpyActivityController.of(ChangePasswordActivity.class).create().get();

        newPassword  =  (EditText)changePasswordActivity.findViewById(R.id.new_password);
        newPasswordConfirm  =  (EditText)changePasswordActivity.findViewById(R.id.new_password_confirm);
        currentPassword  = (EditText)changePasswordActivity.findViewById(R.id.current_password);

        changeButton = (Button) changePasswordActivity.findViewById(R.id.change_button);
    }

    @Test @Ignore
    public void shouldCheckIfMandatoryFieldsAreFilled() {
        newPassword = mock(EditText.class);
        newPasswordConfirm = mock(EditText.class);
        currentPassword = mock(EditText.class);

        doReturn(currentPassword).when(changePasswordActivity).findViewById(R.id.current_password);
        doReturn(newPassword).when(changePasswordActivity).findViewById(R.id.new_password);
        doReturn(newPasswordConfirm).when(changePasswordActivity).findViewById(R.id.new_password_confirm);

        changePasswordActivity.validatesPresenceOfMandatoryFields();
        verify(currentPassword).setError(changePasswordActivity.getString(R.string.mandatory));
        verify(newPassword).setError(changePasswordActivity.getString(R.string.mandatory));
    }    

    @Test
    public void shouldNotSendRequestToServerIfFieldsAreInValid() {
        newPassword = mock(EditText.class);
        newPasswordConfirm = mock(EditText.class);
        currentPassword = mock(EditText.class);

        doReturn(currentPassword).when(changePasswordActivity).findViewById(R.id.current_password);
        doReturn(newPassword).when(changePasswordActivity).findViewById(R.id.new_password);
        doReturn(newPasswordConfirm).when(changePasswordActivity).findViewById(R.id.new_password_confirm);

        changeButton.performClick();
        verify(changePasswordActivity, never()).sendRequestToServer(anyString(),anyString(),anyString());
    }

    @Test
    public void shouldCreateAlertDialogIfNoSyncTaskIsRunningInBackground() {
        doReturn(true).when(changePasswordActivity).validatesPresenceOfMandatoryFields();
        doReturn(true).when(changePasswordActivity).isPasswordSameAsConfirmPassword();

        RapidFtrApplication context = mock(RapidFtrApplication.class);
        when(changePasswordActivity.getContext()).thenReturn(context);
        when(context.getSyncTask()).thenReturn(new SyncAllDataAsyncTask(null,null, null, null,null));

        changePasswordActivity.changePassword(null);
        verify(changePasswordActivity).createAlertDialog();
    }
}
