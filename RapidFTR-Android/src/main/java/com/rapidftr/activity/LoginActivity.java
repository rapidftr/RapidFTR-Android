package com.rapidftr.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.rapidftr.R;
import com.rapidftr.bean.LoginTask;
import org.androidannotations.annotations.*;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;

@EActivity(R.layout.activity_login)
public class LoginActivity extends RapidFtrActivity {

    @Bean @NonConfigurationInstance
    protected LoginTask loginTask;

    @ViewById(R.id.username)
    protected EditText userNameView;

    @ViewById(R.id.password)
    protected EditText passwordView;

    @ViewById(R.id.url)
    protected EditText urlView;

    @ViewById(R.id.change_url)
    protected TextView changeUrlView;

    @AfterViews
    public void afterCreate() {
        toggleBaseUrl();
        goToHomeScreenIfLoggedIn();
    }

    @Click(R.id.change_url)
    public void onChangeUrlClick() {
        urlView.setVisibility(View.VISIBLE);
        changeUrlView.setVisibility(View.GONE);
    }

    @Click(R.id.login_button)
    public void onLoginClick() {
        if (isValid()) {
            String username = userNameView.getText().toString().trim();
            String password = passwordView.getText().toString().trim();
            String url = urlView.getText().toString().trim();
            login(username, password, url);
        }
    }

    @Click(R.id.new_user_signup_link)
    public void onSignupClick() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        goToHomeScreenIfLoggedIn();
    }

    protected void toggleBaseUrl() {
        String preferencesUrl = getContext().getSharedPreferences().getString(SERVER_URL_PREF, null);
        if (preferencesUrl != null && !preferencesUrl.equals("")) {
            urlView.setText(preferencesUrl);
            urlView.setVisibility(View.GONE);
            changeUrlView.setVisibility(View.VISIBLE);
        }
    }

    protected boolean isValid() {
        return !(
            Strings.isNullOrEmpty(userNameView.getText().toString()) ||
            Strings.isNullOrEmpty(passwordView.getText().toString())
        );
    }

    @Background
    protected void login(String username, String password, String baseUrl) {
        loginTask.login(username, password, baseUrl);
        goToHomeScreenIfLoggedIn();
    }

    @UiThread
    protected void goToHomeScreenIfLoggedIn() {
        if (getContext().isLoggedIn()){
	        finish();
            startActivity(new Intent(this, RegisterChildActivity.class));
        } else {
	        Intent broadcastLogout = new Intent(LOGOUT_INTENT_FILTER);
	        sendBroadcast(broadcastLogout);
        }
    }

    @Override
    protected boolean shouldEnsureLoggedIn() {
        return false;
    }

}
