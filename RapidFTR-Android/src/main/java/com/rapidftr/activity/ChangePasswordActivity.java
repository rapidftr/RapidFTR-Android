package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.rapidftr.R;
import com.rapidftr.task.ChangePasswordTask;

public class ChangePasswordActivity extends RapidFtrActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.change_password);
    }

    @Override
    protected boolean shouldEnsureLoggedIn() {
        return true;
    }

    public void changePassword(View view) {
        String old = getEditText(R.id.current_password);
        String new_password = getEditText(R.id.new_password);
        String confirmation = getEditText(R.id.new_password_confirm);

        if(validatesPresenceOfMandatoryFields() && isPasswordSameAsConfirmPassword()) {
            sendRequestToServer(old, new_password, confirmation);
        }
    }

    private boolean isPasswordSameAsConfirmPassword() {
        if (!getEditText(R.id.new_password).equals(getEditText(R.id.new_password_confirm))) {
            ((EditText) findViewById(R.id.new_password_confirm)).setError(getString(R.string.password_mismatch));
            return false;
        }
        return true;
    }

    protected boolean validatesPresenceOfMandatoryFields() {
        return validateTextFieldNotEmpty(R.id.current_password, R.string.mandatory) &
                validateTextFieldNotEmpty(R.id.new_password, R.string.mandatory) &
                validateTextFieldNotEmpty(R.id.new_password_confirm, R.string.mandatory);
    }

    protected void sendRequestToServer(String old_password, String new_password, String confirmation) {
        ChangePasswordTask task = inject(ChangePasswordTask.class);
        task.setActivity(this);
        task.execute(old_password, new_password, confirmation);
    }
}
