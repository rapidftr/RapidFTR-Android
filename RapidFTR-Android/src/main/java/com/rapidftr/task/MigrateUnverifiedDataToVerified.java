package com.rapidftr.task;

import android.os.AsyncTask;
import android.util.Log;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.SQLCipherHelper;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.PhotoCaptureHelper;
import lombok.Cleanup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MigrateUnverifiedDataToVerified extends AsyncTask<Void, Void, Void> {
    private JSONObject responseFromServer;
    private User unVerifiedUser;

    public MigrateUnverifiedDataToVerified(JSONObject responseFromServer, User unVerifiedUser){
        this.responseFromServer = responseFromServer;
        this.unVerifiedUser = unVerifiedUser;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        @Cleanup ChildRepository unverifiedChildRepo = getChildRepo(unVerifiedUser);
        @Cleanup ChildRepository verifiedChildRepo = getChildRepo(getUserFromResponse());
        migrateChildren(unverifiedChildRepo, verifiedChildRepo);
        return null;
    }

    protected ChildRepository getChildRepo(User user) {
        return new ChildRepository(user.getUserName(), new SQLCipherHelper(user, RapidFtrApplication.getApplicationInstance()).getSession());
    }

    private void migrateChildren(ChildRepository unverifiedChildRepo, ChildRepository verifiedChildRepo) {
        try {
            List<Child> children = unverifiedChildRepo.getChildrenByOwner();
            for (Child child : children) {
                verifiedChildRepo.createOrUpdate(child);
                JSONArray photoKeys = child.getPhotos();
                for(int i = 0; i < photoKeys.length(); i++){
                    String photo = photoKeys.getString(i);
                    new PhotoCaptureHelper(RapidFtrApplication.getApplicationInstance()).convertPhoto(photo, unVerifiedUser.getDbKey(), responseFromServer.getString("db_key"));
                }
            }
            unverifiedChildRepo.deleteChildrenByOwner();
            setNewCurrentUser(responseFromServer, unVerifiedUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setNewCurrentUser(JSONObject userFromResponse, User currentUser) throws JSONException {
        currentUser.setDbKey(userFromResponse.getString("db_key"));
        currentUser.setVerified(userFromResponse.getBoolean("user_status"));
        currentUser.setOrganisation(userFromResponse.getString("organisation"));
        currentUser.setLanguage(userFromResponse.getString("language"));
        RapidFtrApplication.getApplicationInstance().setCurrentUser(currentUser);
    }

    protected User getUserFromResponse() {
        User user = new User(unVerifiedUser.getUserName());
        try {
            user.setDbKey(responseFromServer.getString("db_key"));
            user.setVerified(responseFromServer.getBoolean("user_status"));
        } catch (JSONException e) {
            Log.e("Migrate Data", e.getMessage());
            throw new RuntimeException();
        }
        return user;
    }
}
