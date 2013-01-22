package com.rapidftr.task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.MainActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.User;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.utils.EncryptionUtil;
import com.rapidftr.utils.NetworkStatus;
import lombok.Cleanup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;
import static com.rapidftr.RapidFtrApplication.Preference.*;
import static com.rapidftr.utils.http.HttpUtils.getToastMessage;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class LoginAsyncTask extends AsyncTask<String, Void, HttpResponse> {

    private RapidFtrActivity activity;
    private ProgressDialog mProgressDialog;
    private RapidFtrApplication application;

    private String userName;
    private String password;
    private String url;

    public LoginAsyncTask(RapidFtrActivity activity) {
        this.activity = activity;
        this.application = activity.getContext();
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(application.getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected HttpResponse doInBackground(String... params) {
        try {
            this.userName = params[0];
            this.password = params[1];
            this.url      = params[2];
            return NetworkStatus.isOnline(application) ? onlineLogin(params) : null;
        } catch (Exception error) {
            Log.e(APP_IDENTIFIER, "Failed to login", error);
            return null;
        }
    }

    private HttpResponse onlineLogin(String[] params) throws IOException {
        return new LoginService().login(application, params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        mProgressDialog.dismiss();
        int statusCode = response == null ? SC_NOT_FOUND : response.getStatusLine().getStatusCode();

        if (statusCode == SC_CREATED) {
            JSONObject responseJSON = responseJSON(response);
            setDbKey(responseJSON);
            setPreferences(responseJSON);
            setLoggedIn();
            goToHomeScreen();
        }
        if ((response == null || isUserSignedUpOnMobile(userName)) && processOfflineLogin(userName, password)) {
            setOfflinePreferences();
            setLoggedIn();
            statusCode = HttpStatus.SC_CREATED;
            goToHomeScreen();
        } else if (response == null) {
            statusCode = HttpStatus.SC_UNAUTHORIZED;
        }

        Toast.makeText(application, getToastMessage(statusCode), Toast.LENGTH_LONG).show();
    }

    private void setOfflinePreferences() {
        if (application.getPreference(FORM_SECTION) == null) {
            try {
                application.setPreference(FORM_SECTION, getFormSectionsFromRawResource());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setLoggedIn() {
        try {
            application.setLoggedIn(true);
            application.setFormSectionsTemplate(application.getPreference(FORM_SECTION));
        } catch (IOException e) {
            Log.e(APP_IDENTIFIER, "Failed to login", e);
        }
    }

    private void setPreferences(JSONObject responseJSON) {
        try {
            String userOrg = getUserOrg(responseJSON);
            application.setPreference(USER_NAME, userName);
            application.setPreference(USER_ORG, userOrg);
            application.setPreference(SERVER_URL, url);
            application.setPreference(userName, (new User(true, EncryptionUtil.encrypt(password, application.getDbKey()), userOrg)).toString());
            application.setPreference(FORM_SECTION, new FormService(application).getPublishedFormSections());
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Failed to set preferences when logging in", e);
        }
    }

    protected boolean processOfflineLogin(String userName, String password) {
        try {
            application.setPreference(USER_NAME, userName);
            User user = application.getUser();

            if (!user.isAuthenticated()) {
                EncryptionUtil.decrypt(password, user.getEncryptedPassword());
                application.setDbKey(user.getDbKey());
                application.setPreference(USER_ORG, user.getOrganisation());
            } else {
                application.setDbKey(decryptDbKey(user.getDbKey(), password));
            }
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Error processing offline login", e);
            return false;
        }
        return true;
    }

    protected String decryptDbKey(String encryptedDbKey, String password) throws Exception {
        return EncryptionUtil.decrypt(password, encryptedDbKey);
    }

    private String getUserOrg(JSONObject responseJSON) {
        try {
            return responseJSON != null ? responseJSON.get("user_org").toString() : null;
        } catch (JSONException e) {
            return null;
        }
    }

    protected void setDbKey(JSONObject responseJSON) {
        try {
            String db_key = responseJSON.get("db_key").toString();
            application.setDbKey(db_key);
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Failed to set DB Key", e);
        }
    }

    private JSONObject responseJSON(HttpResponse response) {
        JSONObject jsonObject = null;
        try {
            String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            jsonObject = new JSONObject(responseAsString);
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Failed to parse login response JSON", e);
        }
        return jsonObject;
    }

    public boolean isUserSignedUpOnMobile(String userName) {
        return application.getPreference(userName) != null;
    }

    protected String getFormSectionsFromRawResource() throws IOException {
        @Cleanup InputStream in = application.getResources().openRawResource(R.raw.form_sections);
        return CharStreams.toString(new InputStreamReader(in));
    }

    protected void goToHomeScreen() {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}

