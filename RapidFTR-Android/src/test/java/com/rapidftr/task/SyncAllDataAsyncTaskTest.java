package com.rapidftr.task;

import android.app.NotificationManager;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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


    @Before
    public void setUp() throws Exception {
        initMocks(this);
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
    public void shouldGetIncomingChildrenFromServerAndSave() throws Exception {
        Child child = mock(Child.class);
        given(childService.getAllChildren()).willReturn(newArrayList(child));

        SyncAllDataAsyncTask syncAllDataAsyncTask = new SyncAllDataAsyncTask(formService, childService, childRepository);
        syncAllDataAsyncTask.setContext(rapidFtrActivity);
        syncAllDataAsyncTask.execute();


        verify(childService).getAllChildren();
        verify(childRepository).createOrUpdate(child);
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
}
