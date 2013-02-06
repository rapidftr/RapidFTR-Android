package com.rapidftr.task;

import android.os.AsyncTask;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.LoginService;
import com.rapidftr.service.RegisterUserService;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.apache.http.HttpStatus.SC_CREATED;

public class RegisterUnverifiedUserAsyncTask extends AsyncTask<String, String, Boolean> {

    private RegisterUserService registerUserService;
    private User user;
    private RapidFtrApplication context;
    private String serverUrl;

    @Inject
    public RegisterUnverifiedUserAsyncTask(RegisterUserService registerUserService, User user, RapidFtrApplication context) {
        this.registerUserService = registerUserService;
        this.user = user;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... url) {
        this.serverUrl = url[0];
        try {
            HttpResponse response = registerUserService.register(user);
            return response.getStatusLine().getStatusCode() == SC_CREATED;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            user.setServerUrl(serverUrl);
        }else{
            context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "").commit();
        }
    }
}
