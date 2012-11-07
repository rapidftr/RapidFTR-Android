package com.rapidftr.model;

import com.google.common.base.Strings;
import com.rapidftr.database.DatabaseHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import static com.rapidftr.database.DatabaseHelper.DB_CHILD_SYNCED;

public class Child extends JSONObject {

    public static final String ID_FIELD = "_id";
    public static final String OWNER_FIELD = "created_by";
    public static final String THUMBNAIL_FIELD = "_thumbnail";
    public static final String[] INTERNAL_FIELDS = { ID_FIELD, OWNER_FIELD, THUMBNAIL_FIELD, DatabaseHelper.DB_CHILD_SYNCED};

    public static final SimpleDateFormat UUID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final ObjectMapper     JSON_MAPPER      = new ObjectMapper();

    public Child() {
    }

    public Child(String content) throws JSONException {
        super(Strings.nullToEmpty(content).trim().length() == 0 ? "{}" : content);
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

    public String getId() throws JSONException {
        return getString(ID_FIELD);
    }

    public void setId(String id) throws JSONException {
        put(ID_FIELD, id);
    }

    public String getOwner() throws JSONException {
        return getString(OWNER_FIELD);
    }

    public void setOwner(String owner) throws JSONException {
        put(OWNER_FIELD, owner);
    }

    public void setSynced(boolean synced) throws JSONException {
        put(DB_CHILD_SYNCED, synced);
    }

    public boolean isSynced() throws JSONException {
        return Boolean.valueOf(get(DB_CHILD_SYNCED).toString());
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

    public void generateUniqueId() throws JSONException {
        if (has(ID_FIELD))
            return;
        else if (!has(OWNER_FIELD))
            throw new IllegalArgumentException("Owner is required for generating ID");
        else
            setId(createUniqueId(Calendar.getInstance()));
    }

    protected String createUniqueId(Calendar calendar) throws JSONException {
        StringBuffer uuid = new StringBuffer(getOwner());
        uuid.append(UUID_DATE_FORMAT.format(calendar.getTime()));
        uuid.append(getUUIDRandom(5));
        return uuid.toString();
    }

    protected String getUUIDRandom(int length) {
        String uuid = String.valueOf(UUID.randomUUID());
        return uuid.substring(uuid.length() - length, uuid.length()); //Fetch last 5 characrters of UUID
    }

    public boolean isValid() {
        int count = names().length();
        for (String field : INTERNAL_FIELDS){
            count = count - (has(field) ? 1 : 0);
        }

        return count > 0;
    }

    public boolean equals(Object other) {
        try {
            return (other != null && other instanceof JSONObject) ? JSON_MAPPER.readTree(toString()).equals(JSON_MAPPER.readTree(other.toString())) : false;
        } catch (IOException e) {
            return false;
        }
    }
}
