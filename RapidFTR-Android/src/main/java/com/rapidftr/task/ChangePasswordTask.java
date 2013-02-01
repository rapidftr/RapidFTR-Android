package com.rapidftr.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.ChangePasswordService;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static org.apache.http.HttpStatus.SC_OK;

public class ChangePasswordTask extends AsyncTask<String, Boolean, Boolean> {

    private ChangePasswordService changePasswordService;
    private String username;
    private RapidFtrApplication context;
    private Activity activity;
    private String currentPassword;
    private String newPassword;


    @Inject
    public ChangePasswordTask(ChangePasswordService changePasswordService, RapidFtrApplication context) {
        this.changePasswordService = changePasswordService;
        this.context = context;
        this.username = context.getCurrentUser().getUserName();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            currentPassword = params[0];
            newPassword = params[1];
            HttpResponse response = changePasswordService.updatePassword(currentPassword, newPassword , params[2]);
            return response.getStatusLine().getStatusCode() == SC_OK;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            activity.finish();
            try {
                User user = new User(this.username, this.currentPassword).load();
                user.setPassword(newPassword);
            } catch (Exception e) {
                Log.e("ChangePasswordTask",e.getMessage());
            }
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.password_change_success, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.password_change_failed, Toast.LENGTH_LONG).show();
        }
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
