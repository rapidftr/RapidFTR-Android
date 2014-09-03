package com.rapidftr.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.PotentialMatch;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class PotentialMatchRepository implements Closeable, Repository<PotentialMatch>{

    private final String userName;
    private final DatabaseSession session;

    @Inject
    public PotentialMatchRepository(@Named("USER_NAME") String userName, DatabaseSession session) {
        this.userName = userName;
        this.session = session;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public List<PotentialMatch> toBeSynced() throws JSONException {
        return null;
    }

    @Override
    public boolean exists(String id) {
        return false;
    }

    @Override
    public PotentialMatch get(String id) throws JSONException {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void createOrUpdate(PotentialMatch potentialMatch) throws JSONException, SQLException {

    }

    @Override
    public HashMap<String, String> getAllIdsAndRevs() throws JSONException {
        return null;
    }

    @Override
    public void update(PotentialMatch potentialMatch) throws JSONException {

    }

    @Override
    public List<PotentialMatch> currentUsersUnsyncedRecords() throws JSONException {
        return null;
    }

    @Override
    public List<String> getRecordIdsByOwner() throws JSONException {
        return null;
    }

    @Override
    public List<PotentialMatch> allCreatedByCurrentUser() throws JSONException {
        return null;
    }
}
