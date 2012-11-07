package com.rapidftr.task;

import android.os.AsyncTask;
import com.rapidftr.service.FormService;

import java.io.IOException;

public class SyncAllDataAsyncTask extends AsyncTask<Object, Void, Boolean> {

    private FormService formService;

    public SyncAllDataAsyncTask(FormService formService) {
        this.formService = formService;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        try {
            formService.getPublishedFormSections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
