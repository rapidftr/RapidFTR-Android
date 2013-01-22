package com.rapidftr.task;

import android.app.NotificationManager;
import android.view.Menu;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class SyncUnverifiedUsersDataAsyncTaskTest {

    @Mock private FormService formService;
    @Mock private ChildService childService;
    @Mock private ChildRepository childRepository;
    @Mock private RapidFtrActivity rapidFtrActivity;
    @Mock private NotificationManager notificationManager;
    @Mock private Menu menu;
    @Mock private MenuItem syncAll;
    @Mock private MenuItem cancelSyncAll;

    SyncUnverifiedUsersDataAsyncTask task;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        given(menu.getItem(0)).willReturn(syncAll);
        given(menu.getItem(0)).willReturn(syncAll);
        doReturn(syncAll).when(menu).getItem(0);
        doReturn(cancelSyncAll).when(menu).getItem(1);
        doReturn(menu).when(rapidFtrActivity).getMenu();

        when(rapidFtrActivity.getSystemService(Matchers.<String>any())).thenReturn(notificationManager);
        when(rapidFtrActivity.getPackageName()).thenReturn("package");

        task = new SyncUnverifiedUsersDataAsyncTask(formService, childService, childRepository);
        task.setContext(rapidFtrActivity);
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

        verify(childService).syncUnverified(child);
    }

    @Test
    public void shouldMarkChildAsSynced() throws Exception {
        Child child = mock(Child.class);
        given(childRepository.currentUsersUnsyncedRecords()).willReturn(newArrayList(child));
        doNothing().when(childService).syncUnverified(child);

        task.onPreExecute();
        task.execute();

        verify(child).setSynced(true);
        verify(childRepository).update(child);
    }
}