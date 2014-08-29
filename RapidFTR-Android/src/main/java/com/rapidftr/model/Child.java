package com.rapidftr.model;

import android.os.Parcel;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn;
import static com.rapidftr.database.Database.ChildTableColumn.*;
import static com.rapidftr.utils.JSONArrays.asList;

public class Child extends BaseModel {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    public static final String CHILD_FORM_NAME = "Children";

    public Child() {
        super();
    }

    public Child(Parcel parcel) throws JSONException {
        this(parcel.readString());
    }

    public Child(String id, String createdBy, String content) throws JSONException {
        super(id, createdBy, content);
    }

    public Child(String id, String createdBy, String content, boolean synced) throws JSONException {
        super(id, createdBy, content);
        setSynced(synced);
    }

    public Child(String content) throws JSONException {
        super(content);
        setHistories();
    }

    public Child(String content, boolean synced) throws JSONException {
        this(content);
        setSynced(synced);
    }


    public String getInternalId() throws JSONException {
        return getString(internal_id.getColumnName());
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

    public boolean isValid() {
        int numberOfNonInternalFields = names().length();

        for (ChildTableColumn field : ChildTableColumn.internalFields()) {
            if (has(field.getColumnName())) {
                numberOfNonInternalFields--;
            }
        }
        return numberOfNonInternalFields > 0;
    }

    public boolean equals(Object other) {
        try {
            return (other != null && other instanceof JSONObject) && JSON_MAPPER.readTree(toString()).equals(JSON_MAPPER.readTree(other.toString()));
        } catch (IOException e) {
            return false;
        }
    }

    public JSONObject values() throws JSONException {
        List<Object> names = asList(names());
        Iterable<Object> systemFields = Iterables.transform(ChildTableColumn.systemFields(), new Function<ChildTableColumn, Object>() {
            @Override
            public Object apply(ChildTableColumn childTableColumn) {
                return childTableColumn.getColumnName();
            }
        });

        Iterables.removeAll(names, Lists.newArrayList(systemFields));
        return new JSONObject(this, names.toArray(new String[names.size()]));
    }

    @Override
    public boolean isSynced() {
        return super.isSynced();
    }


    public boolean isNew() {
        return !has(internal_id.getColumnName());
    }

    public JSONArray getPhotos() {
        JSONArray photo_keys = new JSONArray();
        try {
            photo_keys = getJSONArray("photo_keys");
        } catch (JSONException e) {
            Log.e("Fetching photos", "photo_keys field is available");
        }
        return photo_keys;
    }
}
