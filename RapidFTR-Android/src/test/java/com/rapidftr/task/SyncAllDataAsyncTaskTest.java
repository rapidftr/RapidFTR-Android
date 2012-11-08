package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class SyncAllDataAsyncTaskTest {

    @Test
    public void shouldSyncFormsAndChildren() throws Exception {
        FormService formService = mock(FormService.class);
        ChildService childService = mock(ChildService.class);
        Child child1 = mock(Child.class);
        Child child2 = mock(Child.class);
        new SyncAllDataAsyncTask(formService, childService).execute(child1, child2);

        verify(formService).getPublishedFormSections();
        verify(childService).post(child1);
        verify(childService).post(child2);
    }
}
