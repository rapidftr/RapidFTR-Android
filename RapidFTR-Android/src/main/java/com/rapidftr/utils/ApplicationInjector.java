package com.rapidftr.utils;

import android.content.Context;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.SQLCipherHelper;
import com.rapidftr.database.SQLCipherSession;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.*;
import com.rapidftr.task.*;
import com.rapidftr.utils.http.FluentRequest;
import org.json.JSONException;

public class ApplicationInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Context.class).to(RapidFtrApplication.class);
        bind(DatabaseHelper.class).to(SQLCipherHelper.class);
        bind(ChildRepository.class);
        bind(FormService.class);
        bind(SyncAllDataAsyncTask.class);
        bind(RegisterUserService.class);
        bind(RegisterUnverifiedUserAsyncTask.class);
        bind(SyncUnverifiedDataAsyncTask.class);
        bind(FluentRequest.class);
        bind(ChildService.class);
        bind(LogOutService.class);
        bind(LoginService.class);
        bind(SyncChildTask.class);
        bind(DeviceService.class);
    }

    @Provides @Named("USER_NAME")
    public String getUserName(User user) {
        return user.getUserName();
    }

    @Provides
    public RapidFtrApplication getRapidFTRApplication() {
        return RapidFtrApplication.getApplicationInstance();
    }

    @Provides
    public DatabaseSession getDatabaseSession(DatabaseHelper helper) {
        return helper.getSession();
    }

    @Provides
    public User getUser(RapidFtrApplication application) throws JSONException {
        return application.isLoggedIn() ? application.getCurrentUser() : null;
    }

    @Provides
    public SynchronisationAsyncTask getSynchronisationAsyncTask(User user, Provider<SyncAllDataAsyncTask> provider1, Provider<SyncUnverifiedDataAsyncTask> provider2) {
        return user.isVerified() ? provider1.get() : provider2.get();
    }


}
