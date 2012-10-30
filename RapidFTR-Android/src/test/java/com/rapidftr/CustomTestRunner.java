package com.rapidftr;

import android.app.Application;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.utils.ApplicationInjector;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

public class CustomTestRunner extends RobolectricTestRunner {

    public static class TestInjector extends AbstractModule {
        @Override
        protected void configure() {
            bindConstant().annotatedWith(Names.named("USER_NAME")).to("user1");
            bindConstant().annotatedWith(Names.named("DB_KEY")).to("db_key");
        }

        @Provides
        public DatabaseHelper getDatabaseHelper() {
            return ShadowSQLiteHelper.getInstance();
        }
    }

    public static Injector INJECTOR = Guice.createInjector(Modules.override(new ApplicationInjector()).with(new TestInjector()));

    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        MockitoAnnotations.initMocks(testClass);
    }

    @Override
    protected Application createApplication() {
        return new RapidFtrApplication(INJECTOR);
    }
}
