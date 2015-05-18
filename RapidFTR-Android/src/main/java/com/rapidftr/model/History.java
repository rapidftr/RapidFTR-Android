package com.rapidftr.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.Lists;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class History extends JSONObject implements Parcelable {
    public static final String HISTORIES = "histories";
    public static final String USER_NAME = "user_name";
    public static final String USER_ORGANISATION = "user_organisation";
    public static final String DATETIME = "datetime";
    public static final String CHANGES = "changes";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String CREATED = "created";

    public History(String jsonSource) throws JSONException {
        super(jsonSource);
    }

    public History() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.toString());
    }

    public static History buildHistoryBetween(RapidFtrApplication rapidFtrApplication, BaseModel originalModel, BaseModel updatedModel) throws JSONException {
        History history = new History();
        addChangesForOldValues(originalModel, updatedModel, history);
        addChangesForNewValues(originalModel, updatedModel, history);
        User currentUser = rapidFtrApplication.getCurrentUser();
        String organisation = currentUser.getOrganisation();
        String userName = currentUser.getUserName();
        history.put(History.USER_NAME, userName);
        history.put(History.USER_ORGANISATION, organisation);
        history.put(History.DATETIME, RapidFtrDateTime.now().defaultFormat());
        return history;
    }

    private static void addChangesForOldValues(BaseModel originalModel, BaseModel updatedModel, History history) throws JSONException {
        List<String> updatedKeys = Lists.newArrayList(updatedModel.keys());
        for (String key : (List<String>) Lists.newArrayList(originalModel.keys())) {
            boolean valueChanged = updatedKeys.contains(key) && !originalModel.get(key).equals(updatedModel.get(key));
            if(shouldRecordHistoryForKey(key) && valueChanged) {
                history.addChangeForValues(key, originalModel.get(key), updatedModel.get(key));
            }

            boolean valueDeleted = !updatedKeys.contains(key) && !originalModel.get(key).equals("");
            if(shouldRecordHistoryForKey(key) && valueDeleted) {
                history.addChangeForValues(key, originalModel.get(key), "");
            }
        }
    }

    private static void addChangesForNewValues(BaseModel originalModel, BaseModel updatedModel, History history) throws JSONException {
        List<String> originalKeys = Lists.newArrayList(originalModel.keys());
        for (String key : (List<String>) Lists.newArrayList(updatedModel.keys())) {
            boolean newValue = !originalKeys.contains(key) && !updatedModel.get(key).equals("");
            if(shouldRecordHistoryForKey(key) && newValue) {
                history.addChangeForValues(key, "", updatedModel.get(key));
            }
        }
    }

    private static boolean shouldRecordHistoryForKey(String key) {
        List<String> keysToIgnore = new ArrayList<String>();
        keysToIgnore.add(Database.ChildTableColumn.synced.getColumnName());
        keysToIgnore.add(Database.ChildTableColumn.last_synced_at.getColumnName());
        keysToIgnore.add(Database.ChildTableColumn.last_updated_at.getColumnName());
        keysToIgnore.add(History.HISTORIES);

        return !keysToIgnore.contains(key);
    }

    private void addChangeForValues(String key, Object oldValue, Object newValue) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FROM, oldValue);
        jsonObject.put(TO, newValue);
        addChange(key, jsonObject);
    }

    public void addChange(String key, JSONObject changes) throws JSONException {
        JSONObject jsonChanges = new JSONObject();
        if(this.has(CHANGES)) {
            jsonChanges = (JSONObject) this.get(CHANGES);
        }
        jsonChanges.put(key, changes);
        this.put(CHANGES, jsonChanges);
    }

    public static History buildCreationHistory(BaseModel model, User user) throws JSONException {
        History creationHistory = new History();
        creationHistory.put(USER_NAME, user.getUserName());
        creationHistory.put(USER_ORGANISATION, user.getOrganisation());
        creationHistory.put(DATETIME, RapidFtrDateTime.now());

        JSONObject createdChange = new JSONObject().put(CREATED, RapidFtrDateTime.now().toString());
        JSONObject changes = new JSONObject();
        changes.put(model.getClass().getSimpleName().toLowerCase(), createdChange);
        creationHistory.put(CHANGES, changes);
        return creationHistory;
    }
}
