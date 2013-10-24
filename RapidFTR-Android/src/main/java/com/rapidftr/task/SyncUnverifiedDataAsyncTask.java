package com.rapidftr.task;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.service.RegisterUserService;
import com.rapidftr.service.SyncService;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.apache.http.HttpStatus.SC_OK;

public class SyncUnverifiedDataAsyncTask<T extends BaseModel> extends SynchronisationAsyncTask<T> {

    private LoginService loginService;
    private RegisterUserService registerUserService;
    private RapidFtrApplication applicationContext;

    @Inject
    public SyncUnverifiedDataAsyncTask(FormService formService,
                                       SyncService<T> recordService,
                                       Repository<T> repository,
                                       LoginService loginService,
                                       RegisterUserService registerUserService,
                                       User user) {

        super(formService, recordService, repository, user);
        this.loginService = loginService;
        this.registerUserService = registerUserService;
    }

    @Override
    public void setContext(RapidFtrActivity context) {
        this.context = context;
        this.applicationContext = context.getContext();
    }

    protected void sync() throws JSONException, IOException, HttpException {
        setProgressAndNotify(context.getString(R.string.synchronize_step_1), 0);
        registerUser();
        JSONObject response = login();
        RapidFtrApplication application = RapidFtrApplication.getApplicationInstance();
        if(response != null && response.optBoolean("verified") && !application.getCurrentUser().isVerified()){
            startMigrationTask(response, application);

        }
        getFormSections();
        sendRecordsToServer(repository.currentUsersUnsyncedRecords());
        List<String> idsToDownload = recordSyncService.getIdsToDownload();
        List<T> recordsToSyncWithServer = repository.toBeSynced();

        if(!application.getCurrentUser().isVerified()) {
            List<String> idsOfCurrentUser = repository.getRecordIdsByOwner();
            Iterator<String> idIterator = idsToDownload.iterator();
            while (idIterator.hasNext()) {
                String id = idIterator.next();
                if(!idsOfCurrentUser.contains(id)) {
                    idIterator.remove();
                }
            }
        }
        int startProgressForDownloadingChildren = formSectionProgress + recordsToSyncWithServer.size();
        saveIncomingRecords(idsToDownload, startProgressForDownloadingChildren);
        setProgressAndNotify(context.getString(R.string.sync_complete), maxProgress);
    }

    protected void startMigrationTask(JSONObject response, RapidFtrApplication application) {
        new MigrateUnverifiedDataToVerified(response, application.getCurrentUser()).execute();
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

}
