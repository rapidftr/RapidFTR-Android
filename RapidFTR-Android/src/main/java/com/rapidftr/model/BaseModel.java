package com.rapidftr.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.rapidftr.database.Database.ChildTableColumn.*;
import static com.rapidftr.utils.JSONArrays.asJSONArray;
import static com.rapidftr.utils.JSONArrays.asList;

public class BaseModel extends JSONObject implements Parcelable {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static final String FIELD_INTERNAL_ID = "_id";
    public static final String FIELD_REVISION_ID = "_rev";
    public static final String EMPTY_STRING = "";

    public BaseModel(String content) throws JSONException {
        super(Strings.nullToEmpty(content).trim().length() == 0 ? "{}" : content);
        if (!has(created_at.getColumnName())) {
            setCreatedAt(RapidFtrDateTime.now().defaultFormat());
        }
        if (!has(Database.ChildTableColumn.synced.getColumnName())) {
            setSynced(false);
        }
        if (!has(Database.ChildTableColumn.unique_identifier.getColumnName())) {
            setUniqueId(createUniqueId());
        }
    }

    public BaseModel() {
        try {
            setSynced(false);
            setCreatedAt(RapidFtrDateTime.now().defaultFormat());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public BaseModel(String id, String owner, String content) throws JSONException {
        this(content);
        setUniqueId(id);
        setCreatedBy(owner);
        if (!has(Database.ChildTableColumn.synced.getColumnName())) {
            setSynced(false);
        }
    }

    public String getUniqueId() throws JSONException {
        return has(unique_identifier.getColumnName()) ? getString(unique_identifier.getColumnName()) : null;
    }

    public String getInternalId() {
        return has(FIELD_INTERNAL_ID) ? getString(FIELD_INTERNAL_ID) : "";
    }

    public void setUniqueId(String id) throws JSONException {
        put(unique_identifier.getColumnName(), id);
    }

    public String getCreatedAt() throws JSONException {
        return getString(created_at.getColumnName());
    }

    protected void setCreatedAt(String createdAt) throws JSONException {
        put(created_at.getColumnName(), createdAt);
    }

    public String getName() {
        return optString(name.getColumnName(), EMPTY_STRING);
    }

    public void setName(String childName) throws JSONException {
        put(name.getColumnName(), childName);
    }

    public String getLastUpdatedAt() throws JSONException {
        return optString(last_updated_at.getColumnName(), null);
    }

    public void setLastUpdatedAt(String lastUpdatedAt) throws JSONException {
        put(last_updated_at.getColumnName(), lastUpdatedAt);
    }

    protected String createUniqueId() throws JSONException {
        return UUID.randomUUID().toString();
    }

    public void generateUniqueId() throws JSONException {
        if (has(unique_identifier.getColumnName())) {
            /* do nothing */
        } else {
            setUniqueId(createUniqueId());
        }
    }

    public void setCreatedBy(String owner) throws JSONException {
        put(created_by.getColumnName(), owner);
    }

    public String getJsonString() {
        return toString();
    }

    public boolean isNew() {
        return !has(internal_id.getColumnName());
    }

    @Override
    public JSONArray names() {
        JSONArray names = super.names();
        return names == null ? new JSONArray() : names;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getJsonString());
    }

    public void removeFromJSONArray(String key, String value) throws JSONException {
        if (!has(key))
            return;

        JSONArray array = getJSONArray(key);
        List<Object> list = asList(array);
        list.remove(value);
        put(key, asJSONArray(list));
    }

    public JSONObject values() throws JSONException {
        return null;
    }

    public String getCreatedBy() throws JSONException {
        return getString(created_by.getColumnName());
    }

    @Override
    public JSONObject put(String key, Object value) {
        if (value != null && value instanceof String) {
            value = Strings.emptyToNull(((String) value).trim());
        } else if (value != null && value instanceof JSONArray && ((JSONArray) value).length() == 0) {
            value = null;
        }
        try {
            return super.put(key, value);
        } catch (JSONException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
        }
        return null;
    }

    public void addToJSONArray(String key, Object element) throws JSONException {
        JSONArray array = has(key) ? getJSONArray(key) : new JSONArray();
        List<Object> list = asList(array);
        if (!list.contains(element))
            list.add(element);

        put(key, asJSONArray(list));
    }

    public String getString(String key) {
        try {
            return super.getString(key);
        } catch (JSONException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
        }
        return null;
    }

    public String getString(String key, String defaultValue) {
        try {
            return super.getString(key);
        } catch (JSONException ex) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, ex.getMessage());
        }
        return defaultValue;
    }

    public void setOrganisation(String userOrg) throws JSONException {
        put(created_organisation.getColumnName(), userOrg);
    }

    public void setSynced(boolean synced) throws JSONException {
        put(Database.ChildTableColumn.synced.getColumnName(), synced);
    }

    public boolean isSynced() {
        return optBoolean(Database.ChildTableColumn.synced.getColumnName());
    }

    public String getShortId() throws JSONException {
        if (!has(unique_identifier.getColumnName()))
            return null;

        int length = getUniqueId().length();
        return length > 7 ? getUniqueId().substring(length - 7) : getUniqueId();
    }

    protected void setHistories() throws JSONException {
        String histories = this.optString(History.HISTORIES, null);
        if (histories != null)
            this.put(History.HISTORIES, new JSONArray(histories));
    }

    public List<BaseModel> getPotentialMatchingModels(PotentialMatchRepository potentialMatchRepo, ChildRepository childRepo, EnquiryRepository enquiryRepository) throws JSONException {
        return new ArrayList<BaseModel>();
    }

    public String getApiPath() {
        return null;
    }

    public String getApiParameter() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        try {
            return (other != null && other instanceof JSONObject) && JSON_MAPPER.readTree(toString()).equals(JSON_MAPPER.readTree(other.toString()));
        } catch (IOException e) {
            return false;
        }
    }

    public List<BaseModel> getConfirmedMatchingModels(PotentialMatchRepository potentialMatchRepository, ChildRepository childRepository, EnquiryRepository enquiryRepository) {
        return new ArrayList<BaseModel>();
    }

    public void addHistory(History history) throws JSONException {
        boolean meaningfulHistory = history.has(History.CHANGES) && history.get(History.CHANGES) != null;
        if(meaningfulHistory) {
            //Android's JSON library does not support 'append', sadly...
            if(has(History.HISTORIES)) {
                accumulate(History.HISTORIES, history);
            } else {
                put(History.HISTORIES, new JSONArray(Lists.newArrayList(history)));
            }
        }
    }

    public void setLastSyncedAt(String lastSyncedAt) throws JSONException {
        put(last_synced_at.getColumnName(), lastSyncedAt);
    }

    public String getLastSyncedAt() throws JSONException {
        return optString(last_synced_at.getColumnName(), null);
    }

    public String getSyncLog() throws JSONException {
        return optString(syncLog.getColumnName(), null);
    }

    public void setSyncLog(String syncLog1) throws JSONException {
        put(syncLog.getColumnName(), syncLog1);
    }

    public String getRecordedAudio() {
        return null;
    }

    public void putRecordedAudio(String fileName) {
        put("recorded_audio", fileName);
    }
}
