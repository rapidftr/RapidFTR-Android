package com.rapidftr.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.view.Menu;
import android.view.MenuItem;
import com.google.common.collect.Maps;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class SyncAllDataAsyncTaskTest {

    @Mock private FormService formService;
    @Mock private ChildService childService;
    @Mock private ChildRepository childRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private RapidFtrActivity rapidFtrActivity;
    @Mock private NotificationManager notificationManager;
    @Mock private Menu menu;
    @Mock private MenuItem syncAll;
    @Mock private MenuItem cancelSyncAll;
    @Mock private User currentUser;
    private SyncAllDataAsyncTask syncTask;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(syncAll).when(menu).getItem(0);
        doReturn(cancelSyncAll).when(menu).getItem(1);
        doReturn(menu).when(rapidFtrActivity).getMenu();

        given(rapidFtrActivity.getSystemService(Matchers.<String>any())).willReturn(notificationManager);

        syncTask = new SyncAllDataAsyncTask(formService, childService, childRepository, currentUser);
    }

    @Test
    public void shouldSyncFormsAndChildren() throws Exception {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        given(childRepository.toBeSynced()).willReturn(newArrayList(child1, child2));
        given(childService.getAllIdsAndRevs()).willReturn(Maps.<String, String>newHashMap());
        syncTask.setContext(rapidFtrActivity);

        syncTask.execute();
        verify(formService).getPublishedFormSections();
        verify(childService).sync(child1, currentUser);
        verify(childService).sync(child2, currentUser);
    }

    @Test
    public void shouldNotSyncFormsIfTaskIsCancelled() throws Exception {
        syncTask.setContext(rapidFtrActivity);
        syncTask = spy(syncTask);
        doReturn(true).when(syncTask).isCancelled();

        syncTask.doInBackground();

        verify(formService, never()).getPublishedFormSections();
    }

    @Test
    public void shouldNotSyncChildrenIfCancelled() throws Exception {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        given(childRepository.toBeSynced()).willReturn(newArrayList(child1, child2));

        syncTask.setContext(rapidFtrActivity);
        syncTask = spy(syncTask);
        doReturn(true).when(syncTask).isCancelled();

        syncTask.onPreExecute();
        syncTask.doInBackground();
        verify(childService, never()).sync(child1, currentUser);
        verify(childService, never()).sync(child2, currentUser);
    }

    @Test
    public void shouldNotGetIncomingChildrenFromServerIfCancelled() throws Exception {
        syncTask.setContext(rapidFtrActivity);
        HashMap<String, String> repositoryIDRevs = createRepositoryIdRevMap();
        HashMap<String, String> serverIDRevs = createServerIdRevMap();

        given(childService.getAllIdsAndRevs()).willReturn(serverIDRevs);
        given(childRepository.getAllIdsAndRevs()).willReturn(repositoryIDRevs);

        syncTask = spy(syncTask);
        doReturn(true).when(syncTask).isCancelled();

        syncTask.onPreExecute();
        syncTask.doInBackground();

        verify(childService).getChild(any(String.class));
        verify(childRepository, never()).createOrUpdate((Child) any());
        verify(childService, never()).setPhoto((Child) any());
    }

    @Test
    public void shouldCreateOrUpdateExistingChild() throws Exception {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        HashMap<String, String> repositoryIDRevs = createRepositoryIdRevMap();
        HashMap<String, String> serverIDRevs = createServerIdRevMap();

        given(childService.getAllIdsAndRevs()).willReturn(serverIDRevs);
        given(childRepository.getAllIdsAndRevs()).willReturn(repositoryIDRevs);
        given(child1.getUniqueId()).willReturn("1234");
        given(child2.getUniqueId()).willReturn("5678");

        given(childService.getChild("qwerty0987")).willReturn(child1);
        given(childService.getChild("abcd1234")).willReturn(child2);

        given(childRepository.exists("1234")).willReturn(true);
        given(childRepository.exists("5678")).willReturn(false);

        syncTask.setContext(rapidFtrActivity);
        syncTask.execute();

        verify(childService).getChild("qwerty0987");
        verify(childRepository).update(child1);
        verify(childRepository).createOrUpdate(child2);
    }

    @Test
    public void shouldToggleMenuOnPreExecute(){
        syncTask.setContext(rapidFtrActivity);

        syncTask.onPreExecute();

        verify(syncAll).setVisible(false);
        verify(cancelSyncAll).setVisible(true);
    }

    @Test
    public void shouldToggleMenuOnCancelAndOnPostExecute(){
        syncTask.setContext(rapidFtrActivity);

        syncTask.onPreExecute();

        syncTask.onCancelled();
        verify(syncAll).setVisible(true);
        verify(cancelSyncAll).setVisible(false);

        syncTask.onPreExecute();
        verify(syncAll).setVisible(true);
        verify(cancelSyncAll).setVisible(false);
    }

    @Test
    public void shouldNotCallSetProgressAndNotifyIfCancelled(){
        syncTask.setContext(rapidFtrActivity);
        syncTask = spy(syncTask);

        doReturn(true).when(syncTask).isCancelled();

        syncTask.onPreExecute();
        verify(notificationManager, never()).notify(anyInt(), (Notification) anyObject());
    }

    @Test
    public void shouldCompareAndRetrieveIdsToBeDownloadedFromServer() throws JSONException, IOException {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        HashMap<String, String> repositoryIDRevs = createRepositoryIdRevMap();
        HashMap<String, String> serverIDRevs = createServerIdRevMap();
        given(childRepository.toBeSynced()).willReturn(newArrayList(child1, child2));
        given(childService.getAllChildren()).willReturn(newArrayList(child1));
        given(childService.getAllIdsAndRevs()).willReturn(serverIDRevs);
        given(childRepository.getAllIdsAndRevs()).willReturn(repositoryIDRevs);
        given(childService.getChild("qwerty0987")).willReturn(mock(Child.class));
        given(childService.getChild("abcd1234")).willReturn(mock(Child.class));

        syncTask.setContext(rapidFtrActivity);
        syncTask.execute();

        verify(formService).getPublishedFormSections();
        verify(childService).sync(child1, currentUser);
        verify(childService).sync(child2, currentUser);
        verify(childService).getAllIdsAndRevs();
        verify(childRepository).getAllIdsAndRevs();
        verify(childService).getChild("qwerty0987");
        verify(childService).getChild("abcd1234");
    }

    private HashMap<String, String> createServerIdRevMap() {
        HashMap<String, String> serverIdRev = new HashMap<String, String>();
        serverIdRev.put("qwerty0987", "4-mnbvc");
        serverIdRev.put("abcd5678", "2-zxy765");
        serverIdRev.put("abcd1234", "2-zxy321");
        return serverIdRev;
    }

    private HashMap<String, String> createRepositoryIdRevMap() {
        HashMap<String, String> repositoryIDRevs = new HashMap<String, String>();
        repositoryIDRevs.put("abcd1234", "1-zxy321");
        repositoryIDRevs.put("abcd5678", "2-zxy765");
        repositoryIDRevs.put("abcd7689", "3-cdsf76");
        return repositoryIDRevs;
    }
}
