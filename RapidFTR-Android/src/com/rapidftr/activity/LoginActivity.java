package com.rapidftr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.github.droidfu.concurrent.BetterAsyncTask;
import com.rapidftr.Config;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static com.rapidftr.utils.HttpUtils.getToastMessage;

public class LoginActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(RapidFtrApplication.isLoggedIn()) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
        setContentView(R.layout.login);
        toggleBaseUrl();
        findViewById(R.id.change_url).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String preferencesUrl = getStringFromSharedPreferences("RAPIDFTR_PREFERENCES", "SERVER_URL");
                toggleView(R.id.base_url, View.VISIBLE);
                toggleView(R.id.url_text, View.VISIBLE);
                toggleView(R.id.change_url, View.INVISIBLE);
                if(preferencesUrl != null){
                   setEditText(R.id.base_url, preferencesUrl);
                }
            }
        });
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = getEditText(R.id.username);
                String password = getEditText(R.id.password);
                String baseUrl = getBaseUrl();
                try {
                    login(username, password, baseUrl);
                } catch (IOException e) {
                    logError(e.getMessage());
                    toastMessage("Login Failed: " + e.getMessage());
                }
            }
        });
    }

    private void toggleBaseUrl() {
        String preferencesUrl = getStringFromSharedPreferences("RAPIDFTR_PREFERENCES", "SERVER_URL");
        if(preferencesUrl != null && !preferencesUrl.equals("")){
            toggleView(R.id.base_url, View.INVISIBLE);
            toggleView(R.id.url_text, View.INVISIBLE);
            toggleView(R.id.change_url, View.VISIBLE);
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
        String preferencesUrl = getStringFromSharedPreferences("RAPIDFTR_PREFERENCES", "SERVER_URL");
        String baseUrl = getEditText(R.id.base_url) != null && !getEditText(R.id.base_url).equals("") ? getEditText(R.id.base_url) : preferencesUrl;
        return baseUrl;
    }

    private void login(String username, String password, String baseUrl) throws IOException {
        new LoginAsyncTask(this).execute(username, password, baseUrl);
    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void getFormSectionBody() throws IOException {
        HttpResponse formSectionsResponse = new FormService().getPublishedFormSections(getBaseUrl());
        RapidFtrApplication.setFormSectionsTemplate(IOUtils.toString(formSectionsResponse.getEntity().getContent()));
    }

    private String getEditText(int resId) {
        return ((EditText) findViewById(resId)).getText().toString().trim();
    }

    private void setEditText(int resId, String text){
        ((EditText)findViewById(resId)).setText(text);
    }
    private class LoginAsyncTask extends BetterAsyncTask<String, Void, HttpResponse> {

        public LoginAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected HttpResponse doCheckedInBackground(Context context, String... params) throws Exception {
            return new LoginService().login(context, params[0], params[1], params[2]);
        }

        @Override
        protected void handleError(Context context, Exception error) {
            logError(error.getMessage());
            toastMessage(getString(R.string.server_not_reachable));
        }

        @Override
        protected void after(Context context, HttpResponse response) {
            int statusCode = response.getStatusLine().getStatusCode();
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
            toastMessage(getToastMessage(statusCode, context));
        }
    }

}
