package com.rapidftr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.User;
import com.rapidftr.repository.PotentialMatchRepository;
import com.rapidftr.utils.http.FluentRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class PotentialMatchSyncService implements SyncService {
    private RapidFtrApplication context;
    private PotentialMatchRepository repository;
    private FluentRequest fluentRequest;

    public PotentialMatchSyncService(RapidFtrApplication rapidFtrApplication, PotentialMatchRepository repository, FluentRequest fluentRequest) {
        this.context = rapidFtrApplication;
        this.repository = repository;
        this.fluentRequest = fluentRequest;
    }

    @Override
    public BaseModel sync(BaseModel record, User currentUser) throws IOException, JSONException, HttpException {
        return null;
    }

    @Override
    public BaseModel getRecord(String id) throws IOException, JSONException, HttpException {
        return null;
    }

    public List<String> getIdsToDownload() throws IOException, HttpException, JSONException {
        HashMap<String, String> serverIdsRevs = getAllIdsAndRevs();
        HashMap<String, String> repoIdsAndRevs = repository.getAllIdsAndRevs();

        ArrayList<String> idsToDownload = new ArrayList<String>();
        for (Map.Entry<String, String> serverIdRev : serverIdsRevs.entrySet()) {
            boolean potentialMatchIdExistsInRepo = repoIdsAndRevs.get(serverIdRev.getKey()) != null;
            boolean newRevisionExists = potentialMatchIdExistsInRepo && !repoIdsAndRevs.get(serverIdRev.getKey()).equals(serverIdRev.getValue());
            if (!potentialMatchIdExistsInRepo || newRevisionExists) {
                idsToDownload.add(serverIdRev.getKey());
            }
        }
        return idsToDownload;
    }

    private HashMap<String, String> getAllIdsAndRevs() throws IOException, HttpException {
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpResponse response = fluentRequest.path("/api/potential_matches/ids").context(context).get().ensureSuccess();

        List<Map> idRevs = asList(objectMapper.readValue(response.getEntity().getContent(), Map[].class));
        HashMap<String, String> idRevMapping = new HashMap<String, String>();
        for (Map idRev : idRevs) {
            idRevMapping.put(idRev.get("_id").toString(), idRev.get("_rev").toString());
        }
        return idRevMapping;
    }
    @Override
    public void setMedia(BaseModel baseModel) throws IOException, JSONException {

    }

    @Override
    public int getNotificationId() {
        return 0;
    }

    @Override
    public String getNotificationTitle() {
        return null;
    }
}
