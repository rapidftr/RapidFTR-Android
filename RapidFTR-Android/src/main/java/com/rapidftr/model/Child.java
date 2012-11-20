package com.rapidftr.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.base.Strings;
import com.rapidftr.utils.RapidFtrDateTime;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import static com.rapidftr.database.Database.ChildTableColumn;
import static com.rapidftr.database.Database.ChildTableColumn.*;

public class Child extends JSONObject implements Parcelable, Comparable {

    public static final SimpleDateFormat UUID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final ObjectMapper     JSON_MAPPER      = new ObjectMapper();

    private String createdAt;

    public Child()  {
        try {
            setSynced(false);
            this.createdAt = (has(created_at.getColumnName()) ? getString(created_at.getColumnName()) : RapidFtrDateTime.now().defaultFormat());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public Child(Parcel parcel) throws JSONException {
        this(parcel.readString());
    }

    public Child(String id, String owner, String content) throws JSONException {
        this(id, owner, content, false);
    }

    public Child(String id, String owner, String content, boolean synced) throws JSONException {
        this(content);
        setId(id);
        setOwner(owner);
        setSynced(synced);
    }

    public Child(String content) throws JSONException {
        this(content, false);
    }

    public Child(String content, boolean synced) throws JSONException {
        super(Strings.nullToEmpty(content).trim().length() == 0 ? "{}" : content);
        this.createdAt = (has(created_at.getColumnName()) ? getString(created_at.getColumnName()) : RapidFtrDateTime.now().defaultFormat());
        setSynced(synced);
    }

    @Override
    public JSONArray names() {
        JSONArray names = super.names();
        return names == null ? new JSONArray() : names;
    }

    @Override
    public JSONObject put(String key, Object value) throws JSONException {
        if (value != null && value instanceof String) {
            value = Strings.emptyToNull(((String) value).trim());
        } else if (value != null && value instanceof JSONArray && ((JSONArray) value).length() == 0) {
            value = null;
        }
        return super.put(key, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getJsonString());
    }

    public String getId() throws JSONException {
        return getString(internal_id.getColumnName());
    }

    public void setId(String id) throws JSONException {
        put(internal_id.getColumnName(), id);
    }

    public String getOwner() throws JSONException {
        return getString(created_by.getColumnName());
    }

    public void setOwner(String owner) throws JSONException {
        put(created_by.getColumnName(), owner);
    }

    public void setSynced(boolean synced) throws JSONException {
        put(ChildTableColumn.synced.getColumnName(), synced);
    }

    public boolean isSynced()  {
        return Boolean.valueOf(opt(ChildTableColumn.synced.getColumnName()).toString());
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void addToJSONArray(String key, Object element) throws JSONException {
        JSONArray array = has(key) ? getJSONArray(key) : new JSONArray();
        for (int i=0, j=array.length(); i < j; i++)
            if (element.equals(array.get(i)))
                return;

        array.put(element);
        put(key, array);
    }

    public void removeFromJSONArray(String key, Object element) throws JSONException {
        if (!has(key))
            return;

        JSONArray array = getJSONArray(key);
        JSONArray newArray = new JSONArray();
        for (int i=0, j=array.length(); i < j; i++) {
            if (!element.equals(array.get(i))) {
                newArray.put(array.get(i));
            }
        }

        put(key, newArray);
    }

    public String getJsonString() {
        return toString();
    }

    public void generateUniqueId() throws JSONException {
        if (has(internal_id.getColumnName())){ /*do nothing*/ }
        else if (!has(created_by.getColumnName()))
            throw new IllegalArgumentException("Owner is required for generating ID");
        else
            setId(createUniqueId(Calendar.getInstance()));
    }

    protected String createUniqueId(Calendar calendar) throws JSONException {
        StringBuilder uuid = new StringBuilder(getOwner());
        uuid.append(UUID_DATE_FORMAT.format(calendar.getTime()));
        uuid.append(getUUIDRandom(5));
        return uuid.toString();
    }

    protected String getUUIDRandom(int length) {
        String uuid = String.valueOf(UUID.randomUUID());
        return uuid.substring(uuid.length() - length, uuid.length());
    }

    public boolean isValid() {
        int numberOfNonInternalFields = names().length();

        for (ChildTableColumn field : ChildTableColumn.internalFields()){
            if(has(field.getColumnName())){
                numberOfNonInternalFields--;
            }
        }
        return numberOfNonInternalFields > 0;
    }

    public boolean equals(Object other) {
        try {
            return (other != null && other instanceof JSONObject) ? JSON_MAPPER.readTree(toString()).equals(JSON_MAPPER.readTree(other.toString())) : false;
        } catch (IOException e) {
            return false;
        }
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>() {
        public Child createFromParcel(Parcel pc) {
            try {
                return new Child(pc);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    public String getFromJSON(String key) {
        String result = "";
        try {
            result = (String) get(key);
        } catch (JSONException e) {
            Log.e("ChildJSON", "Doesn't Contains Key : " + key);
        }
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return this.getFromJSON("name").compareTo(((Child) o).getFromJSON("name"));
    }
}
