package com.rapidftr.service;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.task.SyncAllDataAsyncTask;
import org.json.JSONException;

import javax.inject.Inject;

import static com.google.common.collect.Iterables.toArray;

public class DataSynchronisationService {

    private RapidFtrApplication context;
    private ChildRepository childRepository;

    @Inject
    public DataSynchronisationService(RapidFtrApplication context, ChildRepository childRepository) {
        this.context = context;
        this.childRepository = childRepository;
    }

    public void syncAllData() throws JSONException {
        new SyncAllDataAsyncTask(new FormService(context)).execute(toArray(childRepository.toBeSynced(), Child.class));
    }
}