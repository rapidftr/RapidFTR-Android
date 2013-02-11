package com.rapidftr.task;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.apache.http.HttpStatus.SC_OK;

public class SyncUnverifiedDataAsyncTask extends SynchronisationAsyncTask {

    private LoginService loginService;
    private RegisterUserService registerUserService;
    private RapidFtrApplication applicationContext;

    @Inject
    public SyncUnverifiedDataAsyncTask(FormService formService, ChildService childService,
                                       ChildRepository childRepository, LoginService loginService,
                                       RegisterUserService registerUserService,
                                       User user) {
        super(formService, childService, childRepository, user);
        this.loginService = loginService;
        this.registerUserService = registerUserService;
    }

    @Override
    public void setContext(RapidFtrActivity context) {
        this.context = context;
        this.applicationContext = context.getContext();
    }

    protected void sync() throws JSONException, IOException {
        setProgressAndNotify(context.getString(R.string.synchronize_step_1), 0);
        registerUser();
        JSONObject response = login();
        getFormSections();
        sendChildrenToServer(childRepository.currentUsersUnsyncedRecords());
        setProgressAndNotify(context.getString(R.string.sync_complete), maxProgress);
        RapidFtrApplication application = RapidFtrApplication.getApplicationInstance();
        if(response.getBoolean("user_status") && !application.getCurrentUser().isVerified()){
            new MigrateUnverifiedDataToVerified(response, application.getCurrentUser()).execute();
            setNewCurrentUser(response, application.getCurrentUser());
        }

    }

    private void setNewCurrentUser(JSONObject userFromResponse, User currentUser) throws JSONException {
        currentUser.setDbKey(userFromResponse.getString("db_key"));
        currentUser.setVerified(userFromResponse.getBoolean("user_status"));
        currentUser.setOrganisation(userFromResponse.getString("organisation"));
        currentUser.setLanguage(userFromResponse.getString("language"));
    }

    private void registerUser() throws IOException {
        String serverUrl = applicationContext.getSharedPreferences().getString(SERVER_URL_PREF, null);
        HttpResponse response = registerUserService.register(currentUser);
        if (response.getStatusLine().getStatusCode() == SC_OK) {
            currentUser.setServerUrl(serverUrl);
        } else {
            applicationContext.getSharedPreferences().edit().putString(SERVER_URL_PREF, "").commit();
        }
    }

    private JSONObject login() throws IOException, JSONException {
        HttpResponse response = loginService.login(context, currentUser.getUserName(), currentUser.getUnauthenticatedPassword(), currentUser.getServerUrl());
        if(response !=null && (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK))
            return new JSONObject(getResponse(response));
        return null;
    }

    protected String getResponse(HttpResponse response) throws IOException {
        return CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
    }

    private User getUser() {
        return RapidFtrApplication.getApplicationInstance().getCurrentUser();
    }

}
