package com.rapidftr.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.LoginActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.utils.http.FluentRequest;

import java.io.FileReader;
import java.io.IOException;

import static android.widget.Toast.LENGTH_LONG;
import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

public class LogOutService {

    public void attemptLogOut(RapidFtrActivity currentActivity) {
	    if (currentActivity.getContext().getSyncTask() != null)
		    createAlertDialog(currentActivity);
	    else
	        logOut(currentActivity);
    }

    protected void logOut(RapidFtrActivity currentActivity) {
	    try {
		    RapidFtrApplication context = currentActivity.getContext();
		    context.setCurrentUser(null);
		    FluentRequest.getHttpClient().getCookieStore().clear();
		    Toast.makeText(context, R.string.logout_successful, LENGTH_LONG).show();
		    currentActivity.finish();
		    currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
	    } catch (IOException e) {
		    Log.e(APP_IDENTIFIER, "Failed to logout", e);
	    }
    }

    protected void cancelSync(RapidFtrApplication context) {
        RapidFtrApplication.getApplicationInstance().cleanSyncTask();
    }

    protected void createAlertDialog(final RapidFtrActivity currentActivity) {
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
