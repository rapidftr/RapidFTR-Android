package com.rapidftr.service;

import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

public interface SyncService<T extends BaseModel> {

    public T sync(T record, User currentUser) throws IOException, JSONException;

    public T getRecord(String id) throws IOException, JSONException;

    public HashMap<String, String> getAllIdsAndRevs() throws IOException, HttpException;

    public void setMedia(T t) throws IOException, JSONException;


}
