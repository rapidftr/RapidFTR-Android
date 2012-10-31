package com.rapidftr.utils;

import android.content.Context;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.SQLCipherHelper;

public class ApplicationInjector extends AbstractModule {

    public static final String DB_NAME = "rapidftr";

    @Override
    protected void configure() {
        bind(Context.class).to(RapidFtrApplication.class);
        bind(DatabaseHelper.class).to(SQLCipherHelper.class);
    }

    @Provides @Named("USER_NAME")
    public String getUserName(RapidFtrApplication application) {
        return application.getUserName();
    }

    @Provides @Named("DB_KEY")
    public String getDBKey(RapidFtrApplication application) {
        return application.getDbKey();
    }

    @Provides @Named("DB_NAME")
    public String getDBName() {
        return DB_NAME;
    }

    @Provides
    public RapidFtrApplication getRapidFTRApplication() {
        return RapidFtrApplication.getInstance();
    }

    @Provides
    public DatabaseSession getDatabaseSession(DatabaseHelper helper) {
        return helper.getSession();
    }

}
