package com.rapidftr.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.service.DeviceService;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.task.SyncUnverifiedDataAsyncTask;
import com.rapidftr.task.SynchronisationAsyncTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.rapidftr.CustomTestRunner.createUser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(CustomTestRunner.class)
public class ApplicationInjectorTest {

    RapidFtrApplication application;
    @Inject
    private DeviceService deviceService;
    @Inject
    private SynchronisationAsyncTask<Child> childSynchronisationAsyncTask;
    @Inject
    @Named("USER_NAME")
    private String userName;

    @Before
    public void setUp() {

        setupInjection();
        application = RapidFtrApplication.getApplicationInstance();
    }

    private void setupInjection() {
        TestInjectionModule module = new TestInjectionModule();
        module.addBinding(DatabaseHelper.class, ShadowSQLiteHelper.getInstance());
        TestInjectionModule.setUp(this, module);
    }

    @Test
    public void testUserName() throws IOException {
        User user = createUser();
        application.setCurrentUser(user);
        setupInjection();
        String result = userName;
        assertThat(result, is(user.getUserName()));
    }

    @Test
    public void testReturnVerifiedSyncTask() throws Exception {
        User user = createUser();
        user.setVerified(true);
        application.setCurrentUser(user);
        assertThat(childSynchronisationAsyncTask, instanceOf(SyncAllDataAsyncTask.class));
    }

    @Test
    public void testReturnUnverifiedSyncTask() throws Exception {
        User user = createUser();
        user.setVerified(false);
        application.setCurrentUser(user);
        setupInjection();
        assertThat(childSynchronisationAsyncTask, instanceOf(SyncUnverifiedDataAsyncTask.class));
    }

    @Test
    public void testReturnDeviceServiceInstance() {
        Assert.assertThat(deviceService, instanceOf(DeviceService.class));
    }
}
