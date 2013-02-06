package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.rapidftr.R;
import com.rapidftr.task.LoginAsyncTask;

import java.io.IOException;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;

public class LoginActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goToHomeScreenIfLoggedIn();

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
                        String baseUrl = getEditText(R.id.url);
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
        goToHomeScreenIfLoggedIn();
    }

    private void toggleBaseUrl() {
        String preferencesUrl = getContext().getSharedPreferences().getString(SERVER_URL_PREF, null);
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

    protected void login(String username, String password, String baseUrl) throws IOException {
        LoginAsyncTask task = inject(LoginAsyncTask.class);
        task.setActivity(this);
        task.execute(username, password, baseUrl);
    }

    protected void goToHomeScreenIfLoggedIn() {
        if (getContext().isLoggedIn()){
	        finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    protected void setEditText(int resId, String text) {
        ((EditText) findViewById(resId)).setText(text);
    }

    @Override
    protected boolean shouldEnsureLoggedIn() {
        return false;
    }

}
