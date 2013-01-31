package com.rapidftr.task;

import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.service.RegisterUserService;
import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.apache.http.HttpStatus.SC_OK;

public class SyncUnverifiedDataAsyncTask extends SynchronisationAsyncTask {

    private LoginService loginService;
    private RegisterUserService registerUserService;
    private RapidFtrApplication applicationContext;
    private User currentUser;

    @Inject
    public SyncUnverifiedDataAsyncTask(FormService formService, ChildService childService,
                                       ChildRepository childRepository, LoginService loginService,
                                       RegisterUserService registerUserService) {
        super(formService, childService, childRepository);
        this.loginService = loginService;
        this.registerUserService = registerUserService;
    }

    @Override
    public void setContext(RapidFtrActivity context) {
        this.context = context;
        this.applicationContext = context.getContext();
        this.currentUser = applicationContext.getCurrentUser();
    }

    protected void sync() throws JSONException, IOException {
        registerUser();
        login();
        getFormSections();
        sendRecordsToServer();
        setProgressAndNotify(context.getString(R.string.sync_complete), MAX_PROGRESS);
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

    private void login() throws IOException {
        loginService.login(context, currentUser.getUserName(), currentUser.getUnauthenticatedPassword(), currentUser.getServerUrl());
    }

    private void sendRecordsToServer() throws JSONException, IOException {
        List<Child> childrenToSyncWithServer = childRepository.currentUsersUnsyncedRecords();
        for (Child child : childrenToSyncWithServer) {
            childService.syncUnverified(child, currentUser);
            child.setSynced(true);
            childRepository.update(child);
        }
    }

}
