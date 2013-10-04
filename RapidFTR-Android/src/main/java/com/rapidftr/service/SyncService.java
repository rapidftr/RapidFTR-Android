package com.rapidftr.service;

import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface SyncService<T extends BaseModel> {

    public T sync(T record, User currentUser) throws IOException, JSONException;

    public T getRecord(String id) throws IOException, JSONException;

    // @Deprecated
    // public HashMap<String, String> getAllIdsAndRevs() throws IOException, HttpException;

    public List<String> getIdsToDownload() throws IOException, JSONException;

    public void setMedia(T t) throws IOException, JSONException;


}
