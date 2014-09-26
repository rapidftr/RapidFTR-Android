package com.rapidftr.repository;

import com.rapidftr.model.BaseModel;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface Repository<T extends BaseModel> {

    public List<T> toBeSynced() throws JSONException;

    public boolean exists(String id);

    public T get(String id) throws JSONException;

    public int size();

    public void createOrUpdate(T t) throws JSONException, SQLException;

    public HashMap<String, String> getAllIdsAndRevs() throws JSONException;

    public void createOrUpdateWithoutHistory(T t) throws JSONException;

    public List<T> currentUsersUnsyncedRecords() throws JSONException;

    public List<String> getRecordIdsByOwner() throws JSONException;

    public List<T> allCreatedByCurrentUser() throws JSONException;
}
