package com.rapidftr.task;

import android.os.AsyncTask;
import com.rapidftr.model.User;
import org.json.JSONObject;

public class MigrateUnverifiedDataToVerified extends AsyncTask<Void, Void, Void> {
    private JSONObject responseFromServer;
    private User unVerifiedUser;

    public MigrateUnverifiedDataToVerified(JSONObject responseFromServer, User unVerifiedUser){
        this.responseFromServer = responseFromServer;
        this.unVerifiedUser = unVerifiedUser;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
