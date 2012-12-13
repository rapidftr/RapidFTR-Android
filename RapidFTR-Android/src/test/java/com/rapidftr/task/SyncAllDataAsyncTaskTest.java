package com.rapidftr.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.view.Menu;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
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
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class SyncAllDataAsyncTaskTest {

    @Mock
    private FormService formService;
    @Mock
    private ChildService childService;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private RapidFtrActivity rapidFtrActivity;
    @Mock
    private NotificationManager notificationManager;
    @Mock
    private Menu menu;
    @Mock
    private MenuItem syncAll;
    @Mock
    private MenuItem cancelSyncAll;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(syncAll).when(menu).getItem(0);
        doReturn(cancelSyncAll).when(menu).getItem(1);
        doReturn(menu).when(rapidFtrActivity).getMenu();

        when(rapidFtrActivity.getSystemService(Matchers.<String>any())).thenReturn(notificationManager);
        when(rapidFtrActivity.getPackageName()).thenReturn("package");
    }

    @Test
    public void shouldSyncFormsAndChildren() throws Exception {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        given(childRepository.toBeSynced()).willReturn(newArrayList(child1, child2));
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);

        syncAllDataAsyncTask.execute();
        verify(formService).getPublishedFormSections();
        verify(childService).sync(child1);
        verify(childService).sync(child2);
    }

    @Test
    public void shouldNotSyncFormsIfTaskIsCancelled() throws Exception {
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);

        syncAllDataAsyncTask = spy(syncAllDataAsyncTask);
        doReturn(true).when(syncAllDataAsyncTask).isCancelled();

        syncAllDataAsyncTask.doInBackground();

        verify(formService, never()).getPublishedFormSections();
    }

    @Test
    public void shouldNotSyncChildrenIfCancelled() throws Exception {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        given(childRepository.toBeSynced()).willReturn(newArrayList(child1, child2));
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);
        syncAllDataAsyncTask = spy(syncAllDataAsyncTask);
        doReturn(true).when(syncAllDataAsyncTask).isCancelled();

        syncAllDataAsyncTask.onPreExecute();
        syncAllDataAsyncTask.doInBackground();
        verify(childService, never()).sync(child1);
        verify(childService, never()).sync(child2);
    }



    @Test
    public void shouldGetIncomingChildrenFromServerAndSave() throws Exception {
        Child child = mock(Child.class);
        given(childService.getAllChildren()).willReturn(newArrayList(child));

        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);
        syncAllDataAsyncTask.execute();


        verify(childService).getAllChildren();
        verify(childRepository).createOrUpdate(child);
        verify(childService).setPhoto(child);
    }

    @Test
    public void shouldNotGetIncomingChildrenFromServerIfCancelled() throws Exception {
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);

        syncAllDataAsyncTask = spy(syncAllDataAsyncTask);
        doReturn(true).when(syncAllDataAsyncTask).isCancelled();

        syncAllDataAsyncTask.onPreExecute();
        syncAllDataAsyncTask.doInBackground();

        verify(childService).getAllChildren();
        verify(childRepository, never()).createOrUpdate((Child) any());
        verify(childService, never()).setPhoto((Child) any());
    }

    @Test
    public void shouldUpdateExistingChildIfTheyAlreadyExistInDatabase() throws Exception {
        Child child = mock(Child.class);
        given(child.getUniqueId()).willReturn("1234");
        given(childService.getAllChildren()).willReturn(newArrayList(child));
        given(childRepository.exists("1234")).willReturn(true);

        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);
        syncAllDataAsyncTask.execute();

        verify(childService).getAllChildren();
        verify(childRepository).update(child);
    }

    @Test
    public void shouldToggleMenuOnPreExecute(){
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);

        syncAllDataAsyncTask.onPreExecute();
        verify(syncAll).setVisible(false);
        verify(cancelSyncAll).setVisible(true);
    }

    @Test
    public void shouldToggleMenuOnCancelAndOnPostExecute(){
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);
        syncAllDataAsyncTask.onPreExecute();

        syncAllDataAsyncTask.onCancelled();
        verify(syncAll).setVisible(true);
        verify(cancelSyncAll).setVisible(false);

        syncAllDataAsyncTask.onPreExecute();
        verify(syncAll).setVisible(true);
        verify(cancelSyncAll).setVisible(false);
    }

    @Test
    public void shouldNotCallSetProgressAndNotifyIfCancelled(){
        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);

        syncAllDataAsyncTask = spy(syncAllDataAsyncTask);
        doReturn(true).when(syncAllDataAsyncTask).isCancelled();

        syncAllDataAsyncTask.onPreExecute();
        verify(notificationManager, never()).notify(anyInt(), (Notification) anyObject());
    }

}
