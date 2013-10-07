package com.rapidftr.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.base.Strings;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import static com.rapidftr.database.Database.ChildTableColumn.created_at;
import static com.rapidftr.database.Database.ChildTableColumn.created_by;
import static com.rapidftr.database.Database.ChildTableColumn.created_organisation;
import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.database.Database.ChildTableColumn.last_updated_at;
import static com.rapidftr.database.Database.ChildTableColumn.name;
import static com.rapidftr.database.Database.ChildTableColumn.unique_identifier;
import static com.rapidftr.utils.JSONArrays.asJSONArray;
import static com.rapidftr.utils.JSONArrays.asList;

public class BaseModel extends JSONObject implements Parcelable {

    public static final String EMPTY_STRING = "";

    public BaseModel(String content) throws JSONException {
        super(Strings.nullToEmpty(content).trim().length() == 0 ? "{}" : content);
        if (!has(created_at.getColumnName())) {
            setCreatedAt(RapidFtrDateTime.now().defaultFormat());
        }
        if (!has(Database.ChildTableColumn.synced.getColumnName())) {
            setSynced(false);
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

    public void setOrganisation(String userOrg) throws JSONException {
        put(created_organisation.getColumnName(), userOrg);
    }

    public void setSynced(boolean synced) throws JSONException {
        put(Database.ChildTableColumn.synced.getColumnName(), synced);
    }

    public boolean isSynced() {
        return optBoolean(Database.ChildTableColumn.synced.getColumnName());
    }
}
