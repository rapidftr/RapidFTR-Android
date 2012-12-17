package com.rapidftr.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.LoginActivity;
import com.rapidftr.activity.RapidFtrActivity;

import static android.widget.Toast.LENGTH_LONG;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;

public class LogOutService {

    public void attemptLogOut(RapidFtrActivity currentActivity) {
        AsyncTask syncTask = RapidFtrApplication.getApplicationInstance().getSyncTask();
        if (syncTask != null) {
            createAlertDialog(currentActivity, syncTask);
        } else {
            logOut(currentActivity);
        }
    }

    private void logOut(RapidFtrActivity currentActivity) {
        RapidFtrApplication context = currentActivity.getContext();
        context.setLoggedIn(false);
        context.removePreference(USER_NAME);
        context.removePreference(USER_ORG);
        Toast.makeText(context, "You have been logged out.", LENGTH_LONG).show();
        currentActivity.finish();
        currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
    }

    private void createAlertDialog(final RapidFtrActivity currentActivity, final AsyncTask taskToCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);

        builder.setTitle(R.string.log_out);
        builder.setMessage(R.string.confirm_logout_message);
        builder.setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                taskToCancel.cancel(false);
                logOut(currentActivity);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        builder.create().show();
    }

}
