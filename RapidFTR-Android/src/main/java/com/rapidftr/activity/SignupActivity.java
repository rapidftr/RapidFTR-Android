package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.rapidftr.R;
import com.rapidftr.model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SignupActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
    }

    @Override
    protected boolean shouldEnsureLoggedIn() {
        return false;
    }

    public boolean isValid() {
        return validatesPresenceOfMandatoryFields() && isPasswordSameAsConfirmPassword();
    }

    public void createUser(View view) throws IOException, GeneralSecurityException {
        if (isValid()) {
	        User user = buildUser();
            if (user.exists()) {
	            EditText editText = (EditText) findViewById(R.id.username);
	            editText.setError(getString(R.string.username_taken));
	            makeToast(getString(R.string.username_taken));
            } else {
	            user.save();
	            makeToast(getString(R.string.registered) + " "+ getEditText(R.id.username));
	            finish();
            }
        }
    }

	protected User buildUser() {
		User user = new User(getEditText(R.id.username));
		user.setVerified(false);
		user.setFullName(getEditText(R.id.full_name));
		user.setPassword(getEditText(R.id.password));
		user.setUnauthenticatedPassword(getEditText(R.id.password));
		user.setOrganisation(getEditText(R.id.organisation));
		return user;
	}

    protected boolean validatesPresenceOfMandatoryFields() {
        return validateTextFieldNotEmpty(R.id.full_name, R.string.full_name_required) &
                validateTextFieldNotEmpty(R.id.username, R.string.username_required) &
                validateTextFieldNotEmpty(R.id.password, R.string.password_required) &
                validateTextFieldNotEmpty(R.id.confirm_password, R.string.confirm_password_required) &
                validateTextFieldNotEmpty(R.id.organisation, R.string.organisation_required);
    }

    private boolean isPasswordSameAsConfirmPassword() {
        if (!getEditText(R.id.password).equals(getEditText(R.id.confirm_password))) {
            ((EditText) findViewById(R.id.confirm_password)).setError(getString(R.string.password_mismatch));
            return false;
        }
        return true;
    }

}
