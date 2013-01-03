package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.rapidftr.R;

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

    public void createUser(View view) {
        isValid();
    }

    protected boolean validatesPresenceOfMandatoryFields() {
        return validateTextFieldNotEmpty(R.id.username,R.string.username_required) &
                validateTextFieldNotEmpty(R.id.password,R.string.password_required) &
                validateTextFieldNotEmpty(R.id.confirm_password,R.string.confirm_password_required) &
                validateTextFieldNotEmpty(R.id.organisation,R.string.organisation_required);
    }

    private boolean isPasswordSameAsConfirmPassword(){
        if(!getEditText(R.id.password).equals(getEditText(R.id.confirm_password))){
            ((EditText)findViewById(R.id.confirm_password)).setError(getString(R.string.password_mismatch));
            return false;
        }
        return true;
    }

}
