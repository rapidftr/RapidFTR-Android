package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.task.SyncAllDataAsyncTask;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class DataSynchronisationServiceTest {

    @Test
    public void shouldCallSyncTask() throws Exception {
        ChildRepository childRepository = mock(ChildRepository.class);
        SyncAllDataAsyncTask syncTask = mock(SyncAllDataAsyncTask.class);
        Child[] children = new Child[]{};
        given(childRepository.toBeSynced()).willReturn(Arrays.asList(children));

        new DataSynchronisationService(childRepository, syncTask).syncAllData();

        verify(syncTask).execute(children);
    }
}
