package com.rapidftr.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;
import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.apache.http.HttpStatus.SC_CREATED;

public class LoginAsyncTask extends AsyncTask<String, Void, User> {

    protected RapidFtrActivity activity;
    protected ProgressDialog mProgressDialog;
    protected RapidFtrApplication application;
    protected FormService formService;

    protected String userName;
    protected String password;
    protected String url;

    @Inject
    public LoginAsyncTask(RapidFtrApplication application, FormService formService) {
        this.application = application;
        this.formService = formService;
    }

    public void setActivity(RapidFtrActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(activity.getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected User doInBackground(String... params) {
        try {
            this.userName = params[0];
            this.password = params[1];
            this.url      = params[2];

	        return doLogin();
        } catch (Exception error) {
            Log.e(APP_IDENTIFIER, "Failed to login", error);
            return null;
        }
    }

	protected User doLogin() throws IOException, JSONException, GeneralSecurityException {
		return isOnline() ? doOnlineLogin() : doOfflineLogin();
	}

	protected User doOnlineLogin() throws IOException, JSONException, GeneralSecurityException {
		HttpResponse response = new LoginService().login(application, userName, password, url);
		if (response == null || response.getStatusLine() == null || response.getStatusLine().getStatusCode() != SC_CREATED)
			return doOfflineLogin();

		String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
		User user = new User(this.userName, this.password, true, this.url);
		user.read(responseAsString);

        new FormService(application).getPublishedFormSections();

		return user;
	}

	protected User doOfflineLogin() throws GeneralSecurityException, IOException {
		User user = new User(this.userName, this.password);
		user.load();
		user.setUnauthenticatedPassword(this.password);
		return user;
	}

    @Override
    protected void onPostExecute(User user) {
	    if (mProgressDialog != null)
            mProgressDialog.dismiss();

	    try {
		    user.save();
		    application.setCurrentUser(user);
		    Toast.makeText(application, R.string.login_successful, Toast.LENGTH_LONG).show();
		    goToHomeScreen();
	    } catch (Exception e) {
		    Toast.makeText(application, R.string.unauthorized, Toast.LENGTH_LONG).show();
	    }
    }

    protected void goToHomeScreen() {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

	protected boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
	}
}

