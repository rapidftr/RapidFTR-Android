package com.rapidftr.utils;

import android.content.Context;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;

public class ApplicationInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Context.class).to(RapidFtrApplication.class);
    }

    @Provides @Named("USER_NAME")
    public String getUserName(RapidFtrApplication application) {
        return application.getUserName();
    }

    @Provides @Named("DB_KEY")
    public String getDBKey(RapidFtrApplication application) {
        return application.getDbKey();
    }

    @Provides
    public RapidFtrApplication getRapidFTRApplication() {
        return RapidFtrApplication.getContext();
    }

}
