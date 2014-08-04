package com.rapidftr.bean;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.task.MigrateUnverifiedDataToVerified;
import com.rapidftr.utils.http.FluentResponse;
import lombok.Getter;
import lombok.NonNull;
import org.androidannotations.annotations.*;
import org.json.JSONObject;

import java.io.InputStreamReader;

import static com.rapidftr.R.string.*;
import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

@EBean
public class LoginTask {

    protected static class LoginException extends RuntimeException {
        @Getter protected Integer messageId = null;

        public LoginException(String message, Throwable cause) {
            super(message, cause);
        }

        public LoginException(int messageId, Throwable cause) {
            super(cause);
            this.messageId = messageId;
        }
    }

    @Bean
    protected ConnectivityBean connectivityBean;

    @App
    protected RapidFtrApplication application;

    @RootContext
    protected Context activity;

    protected ProgressDialog progressDialog;

    public void login(String userName, String password, String url) {
        createProgressDialog();
        if (!loginOnline(userName, password, url)) {
            loginOffline(userName, password);
        }
        dismissProgressDialog();
    }

    protected boolean loginOnline(String userName, String password, String url) {
        try {
            notifyProgress(login_online_progress);
            User user = loadOnline(userName, password, url);
            migrateIfVerified(user);
            cacheForOffline(user);
            loadFormSections();
            notifyToast(login_online_success);
            return true;
        } catch (LoginException e) {
            Log.e(APP_IDENTIFIER, "Failed to login online", e);
            notifyToast(e);
            return false;
        }
    }

    protected boolean loginOffline(String userName, String password) {
        try {
            notifyProgress(login_offline_progress);
            User user = loadOffline(userName, password);
            cacheForOffline(user);
            notifyToast(login_offline_success);
            return true;
        } catch (LoginException e) {
            Log.e(APP_IDENTIFIER, "Failed to login offline", e);
            notifyToast(e);
            return false;
        }
    }

    protected User loadOnline(String userName, String password, String url) {
        if (!connectivityBean.isOnline()) {
            throw new LoginException(login_online_no_connectivity, null);
        }

        try {
            FluentResponse response = new LoginService().login(application, userName, password, url);
            String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

            if (!response.isSuccess()) {
                throw new LoginException(responseAsString, null);
            }

            return new User(userName, password, true, url).read(responseAsString);
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, LoginException.class);
            throw new LoginException(login_online_failed, null);
        }
    }

    protected User loadOffline(String userName, String password) {
        User user = new User(userName, password);
        if (!user.exists()) {
            throw new LoginException(login_offline_no_user, null);
        }

        try {
            return user.load();
        } catch(Exception e) {
            throw new LoginException(login_offline_failed, e);
        }
    }

    protected void migrateIfVerified(@NonNull User onlineUser) {
        User offlineUser = null;
        try {
            offlineUser = loadOffline(onlineUser.getUserName(), onlineUser.getPassword());
        } catch (LoginException e) {
            return;
        }

        if (offlineUser != null && onlineUser.isVerified() && !offlineUser.isVerified()) {
            try {
                notifyProgress(login_migrate_progress);
                new MigrateUnverifiedDataToVerified(new JSONObject(onlineUser.asJSON()), offlineUser).execute();
            } catch (Exception e) {
                throw new LoginException(login_migrate_failed, null);
            }
        }
    }

    protected void cacheForOffline(@NonNull User user) {
        try {
            user.save();
            application.setCurrentUser(user);
        } catch (Exception e) {
            throw new LoginException(login_save_failed, e);
        }
    }

    protected void loadFormSections() {
        try {
            notifyProgress(login_form_progress);
            new FormService(application).downloadPublishedFormSections();
        } catch (Exception e) {
            throw new LoginException(login_form_failed, e);
        }
    }

    @UiThread
    protected void createProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
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
    public void notifyToast(LoginException e) {
        if (e.getMessageId() != null) {
            notifyToast(e.getMessageId());
        } else {
            Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    public void notifyToast(int message) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
    }

}
