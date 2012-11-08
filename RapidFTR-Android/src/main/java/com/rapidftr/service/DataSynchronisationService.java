package com.rapidftr.service;

import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.task.SyncAllDataAsyncTask;
import org.json.JSONException;

import javax.inject.Inject;

import java.util.List;

import static com.google.common.collect.Iterables.toArray;

public class DataSynchronisationService {

    private ChildRepository childRepository;
    private SyncAllDataAsyncTask syncTask;

    @Inject
    public DataSynchronisationService(ChildRepository childRepository, SyncAllDataAsyncTask syncTask) {
        this.childRepository = childRepository;
        this.syncTask = syncTask;
    }

    public void syncAllData() throws JSONException {
        List<Child> childrenToSync = childRepository.toBeSynced();
        syncTask.execute(toArray(childrenToSync, Child.class));
    }
}