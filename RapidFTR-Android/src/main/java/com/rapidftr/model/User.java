package com.rapidftr.model;

import com.google.common.base.Strings;
import com.rapidftr.database.Database;
import com.rapidftr.utils.EncryptionUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class User extends JSONObject {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public User(Boolean authenticated, String dbKey, String userOrg, String fullName, String password) throws Exception {
        this(authenticated, dbKey, userOrg);
        setFullName(fullName);
        setPassword(password);
    }

    private void setPassword(String password) throws Exception {
        put(Database.UserTableColumn.encryptedPassword.getColumnName(), EncryptionUtil.encryptWithoutASeed(password));
    }

    public User(Boolean authenticated, String dbKey, String userOrg) throws JSONException {
        setAuthenticated(authenticated);
        setDbKey(dbKey);
        setOrganisation(userOrg);
    }


    public User(String jsonContent) throws JSONException {
        super(Strings.nullToEmpty(jsonContent).trim().length() == 0 ? "{}" : jsonContent);
    }

    public boolean equals(Object other) {
        try {
            return (other != null && other instanceof JSONObject) && JSON_MAPPER.readTree(toString()).equals(JSON_MAPPER.readTree(other.toString()));
        } catch (IOException e) {
            return false;
        }
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


    public String getFullName() {
        return optString(Database.UserTableColumn.fullName.getColumnName());
    }

    public void setFullName(String fullName) throws JSONException {
        put(Database.UserTableColumn.fullName.getColumnName(), fullName);
    }

    public String getDbKey() {
        return optString(Database.UserTableColumn.dbKey.getColumnName());
    }

    public void setDbKey(String dbKey) throws JSONException {
        put(Database.UserTableColumn.dbKey.getColumnName(), dbKey);
    }

    public String getOrganisation() {
        return optString(Database.UserTableColumn.organisation.getColumnName());
    }

    public void setOrganisation(String organisation) throws JSONException {
        put(Database.UserTableColumn.organisation.getColumnName(), organisation);
    }

    public Boolean getAuthenticated() {
        return optBoolean(Database.UserTableColumn.authenticated.getColumnName());
    }

    public void setAuthenticated(Boolean authenticated) throws JSONException {
        put(Database.UserTableColumn.authenticated.getColumnName(), authenticated);
    }



}
