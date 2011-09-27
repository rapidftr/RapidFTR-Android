package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
        ((EditText) findViewById(R.id.username)).setHint(DEFAULT_USERNAME);
        ((EditText) findViewById(R.id.password)).setHint(DEFAULT_PASSWORD);
        ((EditText) findViewById(R.id.base_url)).setText(Config.getBaseUrl());
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateBaseUrl();
                String username = getEditText(R.id.username, DEFAULT_USERNAME);
                String password = getEditText(R.id.password, DEFAULT_PASSWORD);
                try {
                    login(username, password);
                } catch (IOException e) {
                    loge(e.getMessage());
                    displayMessage("Login Failed: " + e.getMessage());
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
        HttpResponse response = new LoginService().login(this, username, password);
        boolean success = response.getStatusLine().getStatusCode() == 201;
        displayMessage(success ? "Login Successful" : "Login Failed: " + response.getStatusLine().toString());
        if (success) {
            getFormSectionBody();
            goToHomeScreen();
        }
    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void getFormSectionBody() throws IOException {
        HttpResponse formSectionsResponse = new FormService().getPublishedFormSections();
        RapidFtrApplication.setFormSectionsBody(IOUtils.toString(formSectionsResponse.getEntity().getContent()));
    }


    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String getEditText(int resId, String defaultValue) {
        String currentValue = getEditText(resId);
        return currentValue.equals("") ? defaultValue : currentValue;
    }

    private String getEditText(int resId) {
        return ((EditText) findViewById(resId)).getText().toString().trim();
    }

}
