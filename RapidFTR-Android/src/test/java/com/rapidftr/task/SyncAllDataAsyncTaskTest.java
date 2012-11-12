package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class SyncAllDataAsyncTaskTest {

    private FormService formService;
    private ChildService childService;
    private ChildRepository childRepository;

    @Before
    public void setUp() throws Exception {
        formService = mock(FormService.class);
        childService = mock(ChildService.class);
        childRepository = mock(ChildRepository.class);
    }

    @Test
    public void shouldSyncFormsAndChildren() throws Exception {
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        new SyncAllDataAsyncTask(formService, childService, childRepository).execute(child1, child2);

        verify(formService).getPublishedFormSections();
        verify(childService).sync(child1);
        verify(childService).sync(child2);
    }

    @Test
    public void shouldGetIncomingChildrenFromServerAndSave() throws Exception {
        Child child = mock(Child.class);
        given(childService.getAllChildren()).willReturn(newArrayList(child));

        new SyncAllDataAsyncTask(formService, childService, childRepository).execute(mock(Child.class));

        verify(childService).getAllChildren();
        verify(childRepository).create(child);
    }

    @Test
    public void shouldUpdateExisitngChildIfTheyAlreadyExistInDatabase() throws Exception {
        Child child = mock(Child.class);
        given(child.getId()).willReturn("1234");
        given(childService.getAllChildren()).willReturn(newArrayList(child));
        given(childRepository.exists("1234")).willReturn(true);

        new SyncAllDataAsyncTask(formService, childService, childRepository).execute(mock(Child.class));

        verify(childService).getAllChildren();
        verify(childRepository).update(child);
    }
}
