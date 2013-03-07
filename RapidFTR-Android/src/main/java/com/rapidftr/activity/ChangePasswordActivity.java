package com.rapidftr.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.task.ChangePasswordTask;

public class ChangePasswordActivity extends RapidFtrActivity{
    String old, new_password, confirmation;

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
        old = getEditText(R.id.current_password);
        new_password = getEditText(R.id.new_password);
        confirmation = getEditText(R.id.new_password_confirm);

        if (validatesPresenceOfMandatoryFields() && isPasswordSameAsConfirmPassword()) {
            if (getContext().getSyncTask() != null)
                createAlertDialog();
            else
                sendRequestToServer(old, new_password, confirmation);
        }
    }

    protected void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.change_password);
        builder.setMessage(R.string.confirm_change_password_text);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancelSync(RapidFtrApplication.getApplicationInstance());
                sendRequestToServer(old,new_password,confirmation);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        builder.create().show();
    }
    protected void cancelSync(RapidFtrApplication context) {
        RapidFtrApplication.getApplicationInstance().cleanSyncTask();
    }

    protected boolean isPasswordSameAsConfirmPassword() {
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
        if(RapidFtrApplication.getApplicationInstance().isOnline()){
            task.execute(old_password, new_password, confirmation);
        } else {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.no_connection, Toast.LENGTH_LONG).show();
            this.finish();
        }

    }
}
