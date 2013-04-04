package com.rapidftr.task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.model.User;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;
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
		return application.isOnline() ? doOnlineLogin() : doOfflineLogin();
	}

	protected User doOnlineLogin() throws JSONException, GeneralSecurityException, IOException {
        HttpResponse response;
        try{
		    response = getLoginResponse();
        } catch (Exception e){
            Log.e(APP_IDENTIFIER, "Failed to login", e);
            return doOfflineLogin();
        }

		if (response == null || response.getStatusLine() == null || response.getStatusLine().getStatusCode() != SC_CREATED)
			return doOfflineLogin();

		String responseAsString = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
		User user = new User(this.userName, this.password, true, this.url);
        user.read(responseAsString);
        User userFromSharedPreference = getUserFromPreference();
        if(userFromSharedPreference !=null && (!userFromSharedPreference.isVerified() && user.isVerified()))
            migrateUnverifiedData(responseAsString, userFromSharedPreference);
        return user;
	}

    protected User getUserFromPreference() {
        try {
            return new User(this.userName, this.password).load();
        } catch (Exception e) {
            Log.e(APP_IDENTIFIER, "Not able to get User from preference");
        }
        return null;
    }

    protected void migrateUnverifiedData(String responseAsString, User user) throws JSONException {
        new MigrateUnverifiedDataToVerified(new JSONObject(responseAsString), user).execute();
    }

    protected HttpResponse getLoginResponse() throws IOException {
        return new LoginService().login(application, userName, password, url);
    }

    protected User doOfflineLogin() throws GeneralSecurityException, IOException {
		User user = new User(this.userName, this.password);
		user.load();
		return user;
	}

    @Override
    protected void onPostExecute(User user) {
	    if (mProgressDialog != null)
            mProgressDialog.dismiss();

	    try {
		    if (user == null)
			    throw new GeneralSecurityException();

		    user.save();
            application.setCurrentUser(user);
            getFormSections(user);

            Toast.makeText(application, R.string.login_successful, Toast.LENGTH_LONG).show();
		    goToHomeScreen();
	    } catch (Exception e) {
		    Toast.makeText(application, R.string.unauthorized, Toast.LENGTH_LONG).show();
	    }
    }

    protected void getFormSections(User user) {
        if (application.isOnline() && user.isVerified()) {
            try {
                formService.getPublishedFormSections();
            } catch (Exception e) {
                Log.e(APP_IDENTIFIER, "Failed to download form sections", e);
                Toast.makeText(application, R.string.fetch_form_sections_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void goToHomeScreen() {
	    activity.finish();
        activity.startActivity(new Intent(activity, RegisterChildActivity.class));
    }

}

