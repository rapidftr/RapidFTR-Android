package com.rapidftr.bean;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.common.io.CharStreams;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.task.MigrateUnverifiedDataToVerified;
import com.rapidftr.utils.http.FluentResponse;
import org.androidannotations.annotations.*;
import org.json.JSONObject;

import java.io.InputStreamReader;

import static com.rapidftr.R.string.*;
import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

@EBean
public class LoginTask {

    @Bean
    protected ConnectivityBean connectivityBean;

    @App
    protected RapidFtrApplication application;

    @RootContext
    protected Context activity;

    protected ProgressDialog progressDialog;

    public User login(String userName, String password, String url) {
        createProgressDialog();
        User onlineUser  = loadOnline(userName, password, url);
        User offlineUser = loadOffline(userName, password);
        User finalUser   = finalizeLogin(onlineUser, offlineUser);
        dismissProgressDialog();
        return finalUser;
    }

    protected User loadOnline(String userName, String password, String url) {
        if (!connectivityBean.isOnline()) {
            notifyProgress(login_online_failed);
            return null;
        }

        try {
            FluentResponse response = new LoginService().login(application, userName, password, url);
            String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

            if (!response.isSuccess()) {
                notifyProgress(login_online_failed);
                notifyToast(responseAsString);
                return null;
            }

            return new User(userName, password, true, url).read(responseAsString);
        } catch (Exception e) {
            Log.d(APP_IDENTIFIER, "Online login failed", e);
            notifyToast(login_online_failed);
            return null;
        }
    }

    protected User loadOffline(String userName, String password) {
        try {
            return new User(userName, password).load();
        } catch (Exception e) {
            return null;
        }
    }

    protected User finalizeLogin(User onlineUser, User offlineUser) {
        boolean isMigrated    = migrateIfVerified(onlineUser, offlineUser);
        boolean isOnlineLogin = onlineUser != null && isMigrated;
        User finalUser        = isOnlineLogin ? onlineUser : offlineUser;
        boolean isLoggedIn    = cacheForOffline(finalUser);

        if (isOnlineLogin)
            loadFormSections();

        notifyToast(isLoggedIn ? (isOnlineLogin ? login_online_success : login_offline_success) : login_invalid);
        return isLoggedIn ? finalUser : null;
    }

    protected boolean migrateIfVerified(User onlineUser, User offlineUser) {
        if (onlineUser != null && offlineUser != null && onlineUser.isVerified() && !offlineUser.isVerified()) {
            try {
                notifyProgress(login_migrate_progress);
                new MigrateUnverifiedDataToVerified(new JSONObject(onlineUser.asJSON()), offlineUser).execute();
            } catch (Exception e) {
                Log.e(APP_IDENTIFIER, "Migrate failed", e);
                notifyToast(login_migrate_failed);
                return false;
            }
        }

        return true;
    }

    protected boolean cacheForOffline(User user) {
        if (user == null) return false;
        try {
            user.save();
            application.setCurrentUser(user);
            return true;
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Failed to save user details", e);
            notifyToast(login_save_failed);
            return false;
        }
    }

    protected boolean loadFormSections() {
        try {
            notifyProgress(login_form_progress);
            new FormService(application).getPublishedFormSections();
            return true;
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Failed to get form sections", e);
            notifyToast(login_form_failed);
            return false;
        }
    }

    @UiThread
    protected void createProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(application.getString(login_progress));
        progressDialog.show();
    }

    @UiThread
    protected void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @UiThread
    public void notifyProgress(Integer message) {
        progressDialog.setMessage(application.getString(message));
    }

    @UiThread
    public void notifyToast(Integer resId) {
        Toast.makeText(application, resId, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    public void notifyToast(String message) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
    }

}
