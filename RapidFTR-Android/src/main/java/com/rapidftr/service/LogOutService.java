package com.rapidftr.service;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.LoginActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.task.SyncAllDataAsyncTask;

import static android.widget.Toast.LENGTH_LONG;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;

public class LogOutService {

    public void attemptLogOut(RapidFtrActivity currentActivity) {
        if (RapidFtrApplication.getApplicationInstance().getSyncTask() != null) {
            createAlertDialog(currentActivity);
        } else {
            logOut(currentActivity);
        }
    }

    private void logOut(RapidFtrActivity currentActivity) {
        RapidFtrApplication context = currentActivity.getContext();
        context.setLoggedIn(false);
        removeUserPreferences(context);
        Toast.makeText(context,currentActivity.getString(R.string.logout_successful), LENGTH_LONG).show();
        currentActivity.finish();
        currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
    }

    private void removeUserPreferences(RapidFtrApplication context) {
        context.removePreference(USER_NAME);
    }

    private void cancelSync(RapidFtrApplication context) {
	    if (context.getSyncTask() != null) {
	        context.getSyncTask().cancel(true);
	        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
	        notificationManager.cancel(SyncAllDataAsyncTask.NOTIFICATION_ID);
	    }
    }

    private void createAlertDialog(final RapidFtrActivity currentActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);

        builder.setTitle(R.string.log_out);
        builder.setMessage(R.string.confirm_logout_message);
        builder.setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancelSync(RapidFtrApplication.getApplicationInstance());
                logOut(currentActivity);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        builder.create().show();
    }

}
