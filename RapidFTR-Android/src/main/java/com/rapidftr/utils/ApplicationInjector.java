package com.rapidftr.utils;

import android.content.Context;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.SQLCipherHelper;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.utils.http.FluentRequest;
import org.json.JSONException;

import static com.rapidftr.RapidFtrApplication.Preference.SERVER_URL;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;

public class ApplicationInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Context.class).to(RapidFtrApplication.class);
        bind(DatabaseHelper.class).to(SQLCipherHelper.class);
        bind(ChildRepository.class);
        bind(FormService.class);
        bind(SyncAllDataAsyncTask.class);
        bind(FluentRequest.class);
        bind(ChildService.class);
        bind(LogOutService.class);
    }

    @Provides @Named("USER_NAME")
    public String getUserName(RapidFtrApplication application) {
        return application.getPreference(USER_NAME);
    }

    @Provides @Named("DB_KEY")
    public String getDBKey(RapidFtrApplication application) {
        return application.getDbKey();
    }

    @Provides
    @Named("DB_NAME")
    public String getDBName(RapidFtrApplication application) throws JSONException {
        String userJson = application.getPreference(application.getPreference(USER_NAME));
        User user = new User(userJson);
        if (!user.isAuthenticated()) {
            return "UNAUTHENTICATED_STORE";
        } else {
            //TODO: The below logic should be changed such that the database name should be generated without considering server URL
            String serverUrl = application.getPreference(SERVER_URL);
            return serverUrl.replaceAll("^.+//", "").replaceAll("\\W+", "_");
        }
    }

    @Provides
    public RapidFtrApplication getRapidFTRApplication() {
        return RapidFtrApplication.getApplicationInstance();
    }

    @Provides
    public DatabaseSession getDatabaseSession(DatabaseHelper helper) {
        return helper.getSession();
    }

}
