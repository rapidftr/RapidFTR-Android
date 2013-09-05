package com.rapidftr.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.RapidFtrDateTime;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn;
import static com.rapidftr.database.Database.ChildTableColumn.*;
import static com.rapidftr.model.Child.History.*;
import static com.rapidftr.utils.JSONArrays.asJSONArray;
import static com.rapidftr.utils.JSONArrays.asList;

public class Child extends BaseModel {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public Child() {
      super();
    }

    public Child(Parcel parcel) throws JSONException {
        this(parcel.readString());
    }

    public Child(String id, String owner, String content) throws JSONException {
      super(id, owner, content);
    }

    public Child(String id, String owner, String content, boolean synced) throws JSONException {
        super(id, owner, content);
        setSynced(synced);
    }

    public Child(String content) throws JSONException {
        super(content);
        setHistories();
    }

    private void setHistories() throws JSONException {
        String histories = this.optString(HISTORIES, null);
        if (histories != null)
            this.put(HISTORIES, new JSONArray(histories));
    }

    public Child(String content, boolean synced) throws JSONException {
        this(content);
        setSynced(synced);
    }

    public String getString(String key) {
        try {
            return super.getString(key);
        } catch (JSONException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
        }
        return null;
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


    public String getId() throws JSONException {
        return getString(internal_id.getColumnName());
    }


    public String getShortId() throws JSONException {
        if (!has(unique_identifier.getColumnName()))
            return null;

        int length = getUniqueId().length();
        return length > 7 ? getUniqueId().substring(length - 7) : getUniqueId();
    }

    public void setOrganisation(String userOrg) throws JSONException {
        put(created_organisation.getColumnName(), userOrg);
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

    public void addToJSONArray(String key, Object element) throws JSONException {
        JSONArray array = has(key) ? getJSONArray(key) : new JSONArray();
        List<Object> list = asList(array);
        if (!list.contains(element))
            list.add(element);

        put(key, asJSONArray(list));
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

    public List<History> changeLogs(Child child, JSONArray existingHistories) throws JSONException {
        //fetch all the histories from this child, which are greater than last_synced_at and merge the histories
        JSONArray names = this.names();
        List<History> histories = getHistoriesFromJsonArray(existingHistories);
        try {
            if (!child.optString("last_synced_at").equals("")) {
                Calendar lastSync = RapidFtrDateTime.getDateTime(child.optString("last_synced_at"));
                for (History history : histories) {
                    Calendar lastSavedAt = RapidFtrDateTime.getDateTime((String) history.get("datetime"));
                    if (lastSavedAt.after(lastSync)) {
                        JSONObject changes = (JSONObject) history.get("changes");
                        for (int i = 0; i < names.length(); i++) {
                            String newValue = this.optString(names.getString(i), "");
                            String oldValue = child.optString(names.getString(i), "");
                            if (!oldValue.equals(newValue)) {
                                JSONObject fromTo = new JSONObject();
                                fromTo.put(FROM, oldValue);
                                fromTo.put(TO, newValue);
                                changes.put(names.getString(i), fromTo);
                            }
                        }
                        history.put(History.USER_NAME, RapidFtrApplication.getApplicationInstance().getSharedPreferences().getString("USER_NAME", ""));
                        history.put(USER_ORGANISATION, RapidFtrApplication.getApplicationInstance().getSharedPreferences().getString("USER_ORG", ""));
                        history.put(DATETIME, RapidFtrDateTime.now().defaultFormat());
                        break;
                    }
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return histories;
    }

    private List<History> getHistoriesFromJsonArray(JSONArray histories) throws JSONException {
        List<Object> objects = histories != null ? asList(histories) : new ArrayList<Object>();
        List<History> childHistories = new ArrayList<History>();
        for (Object object : objects) {
            childHistories.add(new History(object.toString()));
        }
        return childHistories;
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

    public class History extends JSONObject implements Parcelable {
        public static final String HISTORIES = "histories";
        public static final String USER_NAME = "user_name";
        public static final String USER_ORGANISATION = "user_organisation";
        public static final String DATETIME = "datetime";
        public static final String CHANGES = "changes";
        public static final String FROM = "from";
        public static final String TO = "to";

        public History(String jsonSource) throws JSONException {
            super(jsonSource);
        }

        public History() {

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(this.toString());
        }

    }
}
