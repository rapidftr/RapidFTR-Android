package com.rapidftr.service;

import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.PotentialMatch;
import com.rapidftr.model.User;
import com.rapidftr.repository.PotentialMatchRepository;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class PotentialMatchSyncService implements SyncService<PotentialMatch> {

    public static final String POTENTIAL_MATCH_API_PATH = "/api/potential_matches";
    public static final String POTENTIAL_MATCH_API_PARAMETER = "potential_match";

    private RapidFtrApplication context;
    private PotentialMatchRepository repository;
    private EntityHttpDao<PotentialMatch> entityHttpDao;

    @Inject
    public PotentialMatchSyncService(RapidFtrApplication rapidFtrApplication, PotentialMatchRepository repository) {
        this.context = rapidFtrApplication;
        this.repository = repository;
        this.entityHttpDao = EntityHttpDaoFactory.createPotentialMatchHttpDao(rapidFtrApplication.getSharedPreferences().getString(RapidFtrApplication.SERVER_URL_PREF, ""),
                POTENTIAL_MATCH_API_PATH, POTENTIAL_MATCH_API_PARAMETER);
    }

    @Override
    public PotentialMatch sync(PotentialMatch record, User currentUser) throws IOException, JSONException, HttpException {
        return null;
    }

    @Override
    public PotentialMatch getRecord(String resourceUrl) throws IOException, JSONException, HttpException {
        PotentialMatch potentialMatch = entityHttpDao.get(resourceUrl);
        potentialMatch.setSynced(true);
        return potentialMatch;
    }

    public List<String> getIdsToDownload() throws IOException, HttpException, JSONException {
        long lastUpdateMillis = context.getSharedPreferences().getLong(RapidFtrApplication.LAST_ENQUIRY_SYNC, 0);  // Default value is currently epoch
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return entityHttpDao.getUpdatedResourceUrls(lastUpdate);
    }

    @Override
    public void setMedia(PotentialMatch potentialMatch) throws IOException, JSONException {
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
