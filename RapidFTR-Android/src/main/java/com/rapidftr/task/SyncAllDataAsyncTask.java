package com.rapidftr.task;

import android.os.AsyncTask;
import com.rapidftr.model.Child;
import com.rapidftr.service.FormService;

import java.io.IOException;

public class SyncAllDataAsyncTask extends AsyncTask<Child, Void, Boolean> {

    private FormService formService;

    public SyncAllDataAsyncTask(FormService formService) {
        this.formService = formService;
    }

    @Override
    protected Boolean doInBackground(Child... children) {
        try {
            formService.getPublishedFormSections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
