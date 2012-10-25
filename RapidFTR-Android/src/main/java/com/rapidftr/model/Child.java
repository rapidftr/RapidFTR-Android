package com.rapidftr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Child extends JSONObject {

    public static final String ID_FIELD = "_id";
    public static final String OWNER_FIELD = "created_by";
    public static final String[] INTERNAL_FIELDS = { ID_FIELD, OWNER_FIELD };

    public static final SimpleDateFormat UUID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public Child() {
    }

    public Child(String content) throws JSONException {
        super(content == null ? "{}" : content);
    }

    public Child(String id, String owner, String content) throws JSONException {
        this(content);
        setId(id);
        setOwner(owner);
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
        for (String field : INTERNAL_FIELDS)
            count = count - (has(field) ? 1 : 0);

        return count > 0;
    }
}
