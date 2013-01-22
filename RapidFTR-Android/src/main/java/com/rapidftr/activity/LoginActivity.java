package com.rapidftr.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.common.io.CharStreams;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.task.LoginAsyncTask;
import lombok.Cleanup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.rapidftr.RapidFtrApplication.Preference.*;
import static com.rapidftr.utils.http.HttpUtils.getToastMessage;

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
        startActivityOn(R.id.new_user_signup_link, SignupActivity.class);
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
                & validateTextFieldNotEmpty(R.id.password, R.string.password_required);
    }


    protected void toggleView(int field, int visibility) {
        View view = findViewById(field);
        view.setVisibility(visibility);
    }

    protected String getBaseUrl() {
        return getEditText(R.id.url);
    }

    protected void login(String username, String password, String baseUrl) throws IOException {
        new LoginAsyncTask(this).execute(username, password, baseUrl);
    }

    public void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    protected void setEditText(int resId, String text) {
        ((EditText) findViewById(resId)).setText(text);
    }

    @Override
    protected boolean shouldEnsureLoggedIn() {
        return false;
    }

}
