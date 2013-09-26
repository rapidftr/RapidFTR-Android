package com.rapidftr.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.task.SyncUnverifiedDataAsyncTask;
import com.rapidftr.task.SynchronisationAsyncTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.rapidftr.CustomTestRunner.createUser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void testUserName() throws IOException {
	    User user = createUser();
	    application.setCurrentUser(user);
        String result = injector.getInstance(Key.get(String.class, Names.named("USER_NAME")));
        assertThat(result, equalTo(user.getUserName()));
    }

    @Test
    public void testReturnVerifiedSyncTask() throws Exception {
	    User user = createUser();
	    user.setVerified(true);
	    application.setCurrentUser(user);
        assertThat(application.getInjector().getInstance(new Key<SynchronisationAsyncTask<Child>>(){}), instanceOf(SyncAllDataAsyncTask.class));
    }

    @Test
    public void testReturnUnverifiedSyncTask() throws Exception {
	    User user = createUser();
	    user.setVerified(false);
	    application.setCurrentUser(user);
        assertThat(application.getInjector().getInstance(new Key<SynchronisationAsyncTask<Child>>(){}), instanceOf(SyncUnverifiedDataAsyncTask.class));
    }

}
