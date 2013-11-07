package com.rapidftr.task;

import android.app.NotificationManager;
import android.view.Menu;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildSyncService;
import com.rapidftr.service.FormService;
import com.rapidftr.service.LoginService;
import com.rapidftr.service.RegisterUserService;
import com.rapidftr.utils.http.FluentResponse;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class SyncUnverifiedDataAsyncTaskTest {

    @Mock private FormService formService;
    @Mock private ChildSyncService childSyncService;
    @Mock private ChildRepository childRepository;
    @Mock private RapidFtrActivity rapidFtrActivity;
    @Mock private NotificationManager notificationManager;
    @Mock private Menu menu;
    @Mock private MenuItem syncAll;
    @Mock private MenuItem cancelSyncAll;
    @Mock private RegisterUserService registerUserService;
    @Mock private LoginService loginService;
    private RapidFtrApplication applicationContext;
    private User currentUser;

    SyncUnverifiedDataAsyncTask task;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        given(menu.getItem(0)).willReturn(syncAll);
        given(menu.getItem(0)).willReturn(syncAll);
        doReturn(syncAll).when(menu).getItem(0);
        doReturn(cancelSyncAll).when(menu).getItem(1);
        doReturn(menu).when(rapidFtrActivity).getMenu();

        given(rapidFtrActivity.getSystemService(Matchers.<String>any())).willReturn(notificationManager);
        given(rapidFtrActivity.getPackageName()).willReturn("package");
        applicationContext = RapidFtrApplication.getApplicationInstance();
        currentUser = new User("username", "password", false, "serverUrl");
        currentUser.setUnauthenticatedPassword("password");
        applicationContext.setCurrentUser(currentUser);
        given(rapidFtrActivity.getContext()).willReturn(applicationContext);
        given(registerUserService.register(any(User.class))).willReturn(new FluentResponse(new TestHttpResponse(200, "")));

        task = new SyncUnverifiedDataAsyncTask<Child>(formService, childSyncService, childRepository, loginService, registerUserService, currentUser);
        task.setContext(rapidFtrActivity);
    }

    @Test
    public void shouldLoginUser() throws Exception {
        task.onPreExecute();
        task.execute();

        verify(loginService).login(rapidFtrActivity, "username", "password", "serverUrl");
    }

    @Test
    public void shouldSyncFormSections() throws Exception {
        task.onPreExecute();
        task.execute();

        verify(formService).getPublishedFormSections();
    }

    @Test
    public void shouldSyncAllChildrenForGivenUser() throws Exception {
        Child child = mock(Child.class);
        given(childRepository.currentUsersUnsyncedRecords()).willReturn(newArrayList(child));

        task.onPreExecute();
        task.execute();

        verify(childSyncService).sync(child, currentUser);
    }
}