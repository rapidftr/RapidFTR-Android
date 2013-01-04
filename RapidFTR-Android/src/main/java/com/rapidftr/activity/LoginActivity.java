package com.rapidftr.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.common.io.CharStreams;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.utils.EncryptionUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.rapidftr.RapidFtrApplication.Preference.*;
import static com.rapidftr.utils.http.HttpUtils.getToastMessage;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class LoginActivity extends RapidFtrActivity {

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext().isLoggedIn()) {
            goToHomeScreen();
        }
        setContentView(R.layout.activity_login);
        toggleBaseUrl();
        startActivityOn(R.id.new_user_signup_link,SignupActivity.class);
        findViewById(R.id.change_url).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleView(R.id.url, View.VISIBLE);
                toggleView(R.id.change_url, View.GONE);
            }
        });
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (isValid()) {
                        String username = getEditText(R.id.username);
                        String password = getEditText(R.id.password);
                        String baseUrl = getBaseUrl();
                        login(username, password, baseUrl);
                    }
                } catch (IOException e) {
                    logError(e.getMessage());
                    makeToast(R.string.internal_error);
                }
            }
        });
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    @Override
    public void onBackPressed() {
        // Suppress the BACK key when this activity is running
        // no-op
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getContext().isLoggedIn()) {
            goToHomeScreen();
        }
    }

    private void toggleBaseUrl() {
        String preferencesUrl = getContext().getPreference(SERVER_URL);
        if (preferencesUrl != null && !preferencesUrl.equals("")) {
            setEditText(R.id.url, preferencesUrl);
            toggleView(R.id.url, View.GONE);
            toggleView(R.id.change_url, View.VISIBLE);
        }
    }

    public boolean isValid() {
        return validateTextFieldNotEmpty(R.id.username, R.string.username_required)
                & validateTextFieldNotEmpty(R.id.password, R.string.password_required)
                & validateTextFieldNotEmpty(R.id.url, R.string.url_required);
    }


    protected void toggleView(int field, int visibility) {
        View view = findViewById(field);
        view.setVisibility(visibility);
    }

    protected String getBaseUrl() {
        return getEditText(R.id.url);
    }

    protected void login(String username, String password, String baseUrl) throws IOException {
        new LoginAsyncTask().execute(username, password, baseUrl);
    }

    protected void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    protected void setEditText(int resId, String text) {
        ((EditText) findViewById(resId)).setText(text);
    }

    @Override
    protected boolean shouldEnsureLoggedIn() {
        return false;
    }

    protected LoginAsyncTask getLoginAsyncTask() {
        return new LoginAsyncTask();
    }

    protected class LoginAsyncTask extends AsyncTask<String, Void, HttpResponse> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage(getString(R.string.loading_message));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            try {
                return new LoginService().login(getApplicationContext(), params[0], params[1], params[2]);
            } catch (Exception error) {
                logError(error.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            int statusCode = response == null ? SC_NOT_FOUND : response.getStatusLine().getStatusCode();
            RapidFtrApplication context = getContext();
            if (statusCode == SC_CREATED) {
                JSONObject responseJSON = responseJSON(response);
                setDbKey(responseJSON);
                setPreferences(responseJSON);
                setContext(context);
                goToHomeScreen();
            }
            if (response == null && processOfflineLogin(getEditText(R.id.username), getEditText(R.id.password))) {
                setContext(context);
                statusCode = HttpStatus.SC_CREATED;
                goToHomeScreen();
            } else if (response == null) {
                statusCode = HttpStatus.SC_UNAUTHORIZED;
            }
            mProgressDialog.dismiss();
            makeToast(getToastMessage(statusCode));
        }

        private void setContext(RapidFtrApplication context) {
            try {
                context.setLoggedIn(true);
                context.setFormSectionsTemplate(context.getPreference(FORM_SECTION));
            } catch (IOException e) {
                logError(e.getMessage());
            }
        }

        private void setPreferences(JSONObject responseJSON) {
            try {
                RapidFtrApplication context = getContext();
                context.setPreference(USER_NAME, getEditText(R.id.username));
                context.setPreference(SERVER_URL, getEditText(R.id.url));
                context.setPreference(getEditText(R.id.username), (new User(true, EncryptionUtil.encrypt(getEditText(R.id.password), context.getDbKey()), getUserOrg(responseJSON))).toString());
                context.setPreference(FORM_SECTION, new FormService(context).getPublishedFormSections());
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }

        protected boolean processOfflineLogin(String userName, String password) {
            try {
                getContext().setPreference(USER_NAME, getEditText(R.id.username));
                String userJson = getContext().getPreference(userName);
                User user = new User(userJson);
                if(!user.isAuthenticated()){
                    EncryptionUtil.decrypt(password, new User(userJson).getEncryptedPassword());
                    getContext().setDbKey(user.getDbKey());
                }else{
                    getContext().setDbKey(decryptDbKey(user.getDbKey(), password));
                }
            } catch (Exception e) {
                logError(e.getMessage());
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
                getContext().setDbKey(db_key);
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }

        private JSONObject responseJSON(HttpResponse response) {
            JSONObject jsonObject = null;
            try {
                String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
                jsonObject = new JSONObject(responseAsString);
            } catch (Exception e) {
                logError(e.getMessage());
            }
            return jsonObject;
        }

    }


}
