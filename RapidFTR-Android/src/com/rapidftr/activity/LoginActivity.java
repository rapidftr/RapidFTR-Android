package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class LoginActivity extends RapidFtrActivity {

    public static final String DEFAULT_USERNAME = "rapidftr";
    public static final String DEFAULT_PASSWORD = "rapidftr";
    public static final String DEFAULT_URL = "dev.rapidftr.com:3000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ((EditText) findViewById(R.id.login_url)).setHint(DEFAULT_URL);
        ((EditText) findViewById(R.id.username)).setHint(DEFAULT_USERNAME);
        ((EditText) findViewById(R.id.password)).setHint(DEFAULT_PASSWORD);
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String loginUrl = getEditText(R.id.login_url, DEFAULT_URL);
                String username = getEditText(R.id.username, DEFAULT_USERNAME);
                String password = getEditText(R.id.password, DEFAULT_PASSWORD);
                try {
                    login(loginUrl, username, password);
                } catch (IOException e) {
                    loge(e.getMessage());
                    displayMessage("Login Failed: " + e.getMessage());
                }
            }
        });
        startActivityOn(R.id.camera_button, CameraPreviewActivity.class);
    }

    //TODO move this out to a service layer
    private void login(String url, String username, String password) throws IOException {
        HttpResponse response = new LoginService().login(this, url, username, password);
        String message = response.getStatusLine().getStatusCode() == 201
                ? "Login Successful" : "Login Failed: " + response.getStatusLine().toString();
        displayMessage(message);

        HttpResponse formSectionsResponse = new FormService().getPublishedFormSections(url);

        String body = IOUtils.toString(formSectionsResponse.getEntity().getContent());
        loge(body);
    }


    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String getEditText(int resId, String defaultValue) {
        String currentValue = ((EditText) findViewById(resId)).getText().toString().trim();
        return currentValue.equals("") ? defaultValue : currentValue;
    }

}
