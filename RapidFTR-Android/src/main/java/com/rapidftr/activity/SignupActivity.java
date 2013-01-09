package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.model.User;

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

    protected boolean isUsernameTakenInMobile(String username) {
        return getContext().getPreference(username) != null;
    }

    public void createUser(View view) throws Exception {
        if (isValid()) {
            if (!isUsernameTakenInMobile(getEditText(R.id.username))) {
                saveUserInSharedPreference();
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(this, getString(R.string.registered) + getEditText(R.id.username), Toast.LENGTH_LONG).show();
            } else {
                EditText editText = (EditText) findViewById(R.id.username);
                editText.setError(getString(R.string.username_taken));
                Toast.makeText(this, getString(R.string.username_taken), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveUserInSharedPreference() throws Exception {
        User user = new User(false, getEditText(R.id.organisation), getEditText(R.id.full_name), getEditText(R.id.password));
        getContext().setPreference(getEditText(R.id.username), user.toString());
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
