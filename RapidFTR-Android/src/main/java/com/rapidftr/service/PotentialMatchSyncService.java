package com.rapidftr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.PotentialMatch;
import com.rapidftr.model.User;
import com.rapidftr.repository.PotentialMatchRepository;
import com.rapidftr.utils.http.FluentRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class PotentialMatchSyncService implements SyncService<PotentialMatch> {
    public static final String ENQUIRY_ID = "enquiry_id";
    public static final String CHILD_ID = "child_id";
    private RapidFtrApplication context;
    private PotentialMatchRepository repository;
    private FluentRequest fluentRequest;

    @Inject
    public PotentialMatchSyncService(RapidFtrApplication rapidFtrApplication, PotentialMatchRepository repository, FluentRequest fluentRequest) {
        this.context = rapidFtrApplication;
        this.repository = repository;
        this.fluentRequest = fluentRequest;
    }

    @Override
    public PotentialMatch sync(PotentialMatch record, User currentUser) throws IOException, JSONException, HttpException {
        return null;
    }

    @Override
    public PotentialMatch getRecord(String id) throws IOException, JSONException, HttpException {
        HttpResponse response = fluentRequest
                .context(context)
                .path(String.format("/api/potential_matches/%s", id))
                .get();
        String potentialMatchJson = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
        return buildPotentialMatch(new JSONObject(potentialMatchJson));
    }

    private PotentialMatch buildPotentialMatch(JSONObject json) throws JSONException {
        PotentialMatch potentialMatch = new PotentialMatch(json.getString(ENQUIRY_ID), json.getString(CHILD_ID), json.getString(PotentialMatch.FIELD_INTERNAL_ID));
        return potentialMatch;
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

    @Override
    public void setMedia(PotentialMatch potentialMatch) throws IOException, JSONException {
    }

    private HashMap<String, String> getAllIdsAndRevs() throws IOException, HttpException {
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpResponse response = fluentRequest.path("/api/potential_matches/ids").context(context).get().ensureSuccess();

        List<Map> idRevs = asList(objectMapper.readValue(response.getEntity().getContent(), Map[].class));
        HashMap<String, String> idRevMapping = new HashMap<String, String>();
        for (Map idRev : idRevs) {
            idRevMapping.put(idRev.get(PotentialMatch.FIELD_INTERNAL_ID).toString(), idRev.get(PotentialMatch.FIELD_REVISION_ID).toString());
        }
        return idRevMapping;
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
