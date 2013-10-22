package com.rapidftr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.SQLCipherHelper;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.Repository;
import com.rapidftr.service.ChildSyncService;
import com.rapidftr.service.DeviceService;
import com.rapidftr.service.EnquirySyncService;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LogOutService;
import com.rapidftr.service.LoginService;
import com.rapidftr.service.RegisterUserService;
import com.rapidftr.service.SyncService;
import com.rapidftr.task.RegisterUnverifiedUserAsyncTask;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.task.SyncChildTask;
import com.rapidftr.task.SyncUnverifiedDataAsyncTask;
import com.rapidftr.task.SynchronisationAsyncTask;
import com.rapidftr.utils.http.FluentRequest;
import com.sun.jersey.api.client.Client;
import org.json.JSONException;

public class ApplicationInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Context.class).to(RapidFtrApplication.class);
        bind(DatabaseHelper.class).to(SQLCipherHelper.class);
        bind(new TypeLiteral<Repository<Child>>(){}).to(ChildRepository.class);
        bind(new TypeLiteral<Repository<Enquiry>>(){}).to(EnquiryRepository.class);
        bind(FormService.class);
        bind(RegisterUserService.class);
        bind(RegisterUnverifiedUserAsyncTask.class);
        bind(FluentRequest.class);
        bind(new TypeLiteral<SyncService<Child>>(){}).to(ChildSyncService.class);
        bind(new TypeLiteral<SyncService<Enquiry>>(){}).to(EnquirySyncService.class);

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
    public SynchronisationAsyncTask<Child> getChildSynchronisationAsyncTask(User user, Provider<SyncAllDataAsyncTask<Child>> provider1, Provider<SyncUnverifiedDataAsyncTask<Child>> provider2) {
        return user.isVerified() ? provider1.get() : provider2.get();
    }

    @Provides
    public SynchronisationAsyncTask<Enquiry> getEnquirySynchronisationAsyncTask(User user, Provider<SyncAllDataAsyncTask<Enquiry>> provider1, Provider<SyncUnverifiedDataAsyncTask<Enquiry>> provider2) {
        return user.isVerified() ? provider1.get() : provider2.get();
    }

    @Provides
    public Client getClient() {
        return Client.create();
    }

    @Provides
    public SharedPreferences getSharedPreferences() {
        return RapidFtrApplication.getApplicationInstance().getSharedPreferences();
    }

}
