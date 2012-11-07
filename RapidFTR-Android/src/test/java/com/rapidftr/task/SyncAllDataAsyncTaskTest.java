package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.service.FormService;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class SyncAllDataAsyncTaskTest {

    @Test
    public void shouldSyncForms() throws Exception {
        FormService formService = mock(FormService.class);
        new SyncAllDataAsyncTask(formService).execute();

        verify(formService).getPublishedFormSections();
    }
}
