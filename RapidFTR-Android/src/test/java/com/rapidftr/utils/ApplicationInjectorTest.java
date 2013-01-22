package com.rapidftr.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.task.SyncUnverifiedUsersDataAsyncTask;
import com.rapidftr.task.SynchronisationAsyncTask;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class ApplicationInjectorTest {

    Injector injector;
    RapidFtrApplication application;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new ApplicationInjector());
        application = RapidFtrApplication.getApplicationInstance();
    }

    @Test
    public void testUserName() {
        application.setPreference(USER_NAME, "test");
        String result = injector.getInstance(Key.get(String.class, Names.named("USER_NAME")));
        assertThat(result, equalTo("test"));
    }

    @Test
    public void testDbNameForAuthenticatedUser() throws JSONException {
        String result = injector.getInstance(Key.get(String.class, Names.named("DB_NAME")));
        assertEquals(result, "DB-" + application.getDbKey().hashCode());
    }

    @Test
    public void testReturnVerifiedSyncTask() throws Exception {
        application.setPreference("testUser", new User(true, "test", "test", "test").toString());
        application.setPreference(USER_NAME, "testUser");
        assertThat(application.getInjector().getInstance(SynchronisationAsyncTask.class), instanceOf(SyncAllDataAsyncTask.class));
    }

    @Test
    public void testReturnUnverifiedSyncTask() throws Exception {
        application.setPreference("testUser", new User(false, "test", "test", "test").toString());
        application.setPreference(USER_NAME, "testUser");
        assertThat(application.getInjector().getInstance(SynchronisationAsyncTask.class), instanceOf(SyncUnverifiedUsersDataAsyncTask.class));
    }

}
