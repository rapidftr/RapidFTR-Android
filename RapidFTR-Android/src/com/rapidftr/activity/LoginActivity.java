package com.rapidftr.activity;

import android.content.Context;
import android.content.Intent;
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

public class LoginActivity extends RapidFtrActivity {

    public static final String DEFAULT_USERNAME = "rapidftr";
    public static final String DEFAULT_PASSWORD = "rapidftr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ((EditText) findViewById(R.id.username)).setText(DEFAULT_USERNAME);
        ((EditText) findViewById(R.id.password)).setText(DEFAULT_PASSWORD);
        ((EditText) findViewById(R.id.base_url)).setText(Config.getBaseUrl());

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateBaseUrl();
                String username = getEditText(R.id.username);
                String password = getEditText(R.id.password);
                try {
                    login(username, password);
                } catch (IOException e) {
                    loge(e.getMessage());
                    toastMessage("Login Failed: " + e.getMessage());
                }
            }
        });
    }

    private void updateBaseUrl() {
        String baseUrl = getEditText(R.id.base_url);
        if (!"".equals(baseUrl)) {
            Config.setBaseUrl(baseUrl);
        }
    }

    private void login(String username, String password) throws IOException {
        new LoginAsyncTask(this).execute(username, password);
    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void getFormSectionBody() throws IOException {
        HttpResponse formSectionsResponse = new FormService().getPublishedFormSections();
        RapidFtrApplication.setFormSectionsTemplate(IOUtils.toString(formSectionsResponse.getEntity().getContent()));
    }

    private String getEditText(int resId) {
        return ((EditText) findViewById(resId)).getText().toString().trim();
    }

    private class LoginAsyncTask extends BetterAsyncTask<String, Void, HttpResponse> {

        public LoginAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected HttpResponse doCheckedInBackground(Context context, String... params) throws Exception {
            return new LoginService().login(context, params[0], params[1]);
        }

        @Override
        protected void handleError(Context context, Exception error) {
            //TODO implement me, figure out exception handling
        }

        @Override
        protected void after(Context context, HttpResponse response) {
            boolean success = response.getStatusLine().getStatusCode() == 201;
            if (success) {
                RapidFtrApplication.setLoggedIn(true);
            }
            toastMessage(success ? "Login Successful" : "Login Failed: " + response.getStatusLine().toString());
            if (success) {
                try {
                    getFormSectionBody();
                } catch (IOException e) {
                    //TODO move getFormSectionBody in an async task as well
                    e.printStackTrace();
                }
                goToHomeScreen();
            }
        }
    }

}
