package com.rapidftr.task;

import android.os.AsyncTask;
import com.google.inject.Inject;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;

public class SyncAllDataAsyncTask extends AsyncTask<Child, Void, Boolean> {

    private FormService formService;
    private ChildService childService;
    private ChildRepository childRepository;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        this.formService = formService;
        this.childService = childService;
        this.childRepository = childRepository;
    }

    @Override
    protected Boolean doInBackground(Child... childrenToSyncWithServer) {
        try {
            formService.getPublishedFormSections();
            sendChildrenToServer(childrenToSyncWithServer);
            saveIncomingChildren();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void sendChildrenToServer(Child[] childrenToSyncWithServer) throws IOException, JSONException {
        for (Child child : childrenToSyncWithServer) {
            childService.sync(child);
        }
    }

    private void saveIncomingChildren() throws IOException, JSONException {
        for (Child incomingChild : childService.getAllChildren()) {
            incomingChild.setSynced(true);
            if(childRepository.exists(incomingChild.getId())){
                childRepository.update(incomingChild);
            }else{
                childRepository.create(incomingChild);
            }
        }
    }
}