package com.rapidftr.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static com.rapidftr.utils.HttpUtils.getToastMessage;

public class LoginActivity extends RapidFtrActivity {

    LoginService loginService;
    private ProgressDialog mProgressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        loginService = new LoginService();
        if(RapidFtrApplication.isLoggedIn()) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
        setContentView(R.layout.login);
        toggleBaseUrl();
        findViewById(R.id.change_url).setOnClickListener(new View.OnClickListener(){
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
                    toastMessage("Login Failed: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Suppress the BACK key when this activity is running
        // no-op
    }

    private void toggleBaseUrl() {
        String preferencesUrl = getStringFromSharedPreferences("RAPIDFTR_PREFERENCES", "SERVER_URL");
        if(preferencesUrl != null && !preferencesUrl.equals("")) {
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

    private boolean validateTextFieldNotEmpty(int id, int messageId) {
        EditText editText = (EditText) findViewById(id);
        String value = getEditText(id);

        if (value == null || "".equals(value)) {
            editText.setError(getString(messageId));
            return false;
        } else {
            return true;
        }
    }

    private void toggleView(int field, int visibility) {
        View view = findViewById(field);
        view.setVisibility(visibility);
    }

    private String getStringFromSharedPreferences(String fileName, String key) {
        SharedPreferences preferences = getApplication().getSharedPreferences(fileName, 0);
        return preferences.getString(key, "");
    }

    private String getBaseUrl() {
        return getEditText(R.id.url);
    }

    private void login(String username, String password, String baseUrl) throws IOException {
        new LoginAsyncTask().execute(username, password, baseUrl);
    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void getFormSectionBody() throws IOException {
        HttpResponse formSectionsResponse = new FormService().getPublishedFormSections(getBaseUrl());
        RapidFtrApplication.setFormSectionsTemplate(IOUtils.toString(formSectionsResponse.getEntity().getContent()));
    }

    public String getEditText(int resId) {
        CharSequence value = ((EditText) findViewById(resId)).getText();
        return value == null ? null : value.toString().trim();
    }

    private void setEditText(int resId, String text){
        ((EditText)findViewById(resId)).setText(text);
    }
    private class LoginAsyncTask extends AsyncTask<String, Void, HttpResponse> {

        @Override
        protected void onPreExecute(){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(getString(R.string.loading_message));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            try {
                return loginService.login(getApplicationContext(), params[0], params[1], params[2]);
            } catch (IOException error) {
                logError(error.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            int statusCode = response == null ? 404 : response.getStatusLine().getStatusCode();
            boolean success = statusCode == 201;
                if (success) {
                RapidFtrApplication.setLoggedIn(true);
            }
            if (success) {
                try {
                    SharedPreferences preferences = getApplication().getSharedPreferences("RAPIDFTR_PREFERENCES",0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SERVER_URL", getBaseUrl());
                    editor.commit();
                    getFormSectionBody();
                } catch (IOException e) {
                    logError(e.getMessage());
                }
                goToHomeScreen();
            }
            mProgressDialog.dismiss();
            toastMessage(getToastMessage(statusCode, context));
        }
    }

}
