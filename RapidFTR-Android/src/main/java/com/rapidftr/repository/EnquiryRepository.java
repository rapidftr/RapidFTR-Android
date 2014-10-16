package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.adapter.pagination.ViewAllChildrenPaginatedScrollListener;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import com.rapidftr.utils.RapidFtrDateTime;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.database.Database.BooleanColumn.falseValue;
import static com.rapidftr.database.Database.EnquiryTableColumn.*;
import static com.rapidftr.database.Database.enquiry;

public class EnquiryRepository implements Closeable, Repository<Enquiry> {

    private final String userName;
    private final DatabaseSession session;

    @Inject
    public EnquiryRepository(@Named("USER_NAME") String userName, DatabaseSession session) {
        this.userName = userName;
        this.session = session;
    }

    @Override
    public void createOrUpdate(Enquiry enquiry) throws JSONException {
        if (exists(enquiry.getUniqueId())) {
            Enquiry existingEnquiry = get(enquiry.getUniqueId());
            enquiry.addHistory(History.buildHistoryBetween(existingEnquiry, enquiry));
        } else {
            User currentUser = RapidFtrApplication.getApplicationInstance().getCurrentUser();
            enquiry.addHistory(History.buildCreationHistory(enquiry, currentUser));
        }
        enquiry.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
        createOrUpdateWithoutHistory(enquiry);
    }

    @Override
    public void createOrUpdateWithoutHistory(Enquiry enquiry) throws JSONException {
        session.replaceOrThrow(Database.enquiry.getTableName(), null, getContentValuesFrom(enquiry));
    }

    protected ContentValues getContentValuesFrom(Enquiry enquiry) throws JSONException {
        ContentValues enquiryValues = new ContentValues();

        enquiryValues.put(id.getColumnName(), enquiry.getUniqueId());
        enquiryValues.put(created_by.getColumnName(), enquiry.getCreatedBy());
        enquiryValues.put(content.getColumnName(), enquiry.getJsonString());
        enquiryValues.put(created_at.getColumnName(), enquiry.getCreatedAt());
        enquiryValues.put(unique_identifier.getColumnName(), enquiry.getUniqueId());
        enquiryValues.put(synced.getColumnName(), enquiry.isSynced());
        enquiryValues.put(internal_id.getColumnName(), enquiry.optString(internal_id.getColumnName()));
        enquiryValues.put(internal_rev.getColumnName(), enquiry.optString(internal_rev.getColumnName()));

        return enquiryValues;
    }

    @Override
    public HashMap<String, String> getAllIdsAndRevs() throws JSONException {
        HashMap<String, String> idRevs = new HashMap<String, String>();
        @Cleanup Cursor cursor = session.rawQuery("SELECT "
                + internal_id.getColumnName() + ", "
                + internal_rev.getColumnName()
                + " FROM " + enquiry.getTableName(), null);
        while (cursor.moveToNext()) {
            idRevs.put(cursor.getString(0), cursor.getString(1));
        }
        return idRevs;
    }

    public List<Enquiry> allCreatedByCurrentUser() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT enquiry_json FROM enquiry WHERE created_by = ? ORDER BY id", new String[]{userName});
        return toEnquiries(cursor);
    }

    @Override
    public List<Enquiry> currentUsersUnsyncedRecords() throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getRecordIdsByOwner() throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Enquiry> toBeSynced() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry WHERE synced" +
                " = ?", new String[]{falseValue.getColumnValue()});
        return toEnquiries(cursor);
    }

    @Override
    public boolean exists(String id) {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry WHERE id = ?", new String[]{id == null ? "" : id});
        return cursor.moveToNext() && cursor.getCount() > 0;
    }

    @Override
    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM enquiry", new String[]{});
        return cursor.moveToNext() ? cursor.getInt(0) : 0;
    }

    @Override
    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Enquiry> all() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry", new String[]{});
        return toEnquiries(cursor);
    }

    List<Enquiry> toEnquiries(Cursor cursor) throws JSONException {
        List<Enquiry> enquiries = new ArrayList<Enquiry>();
        while (cursor.moveToNext()) {
            enquiries.add(buildEnquiry(cursor));
        }
        return enquiries;
    }

    //TODO move this to the enquiry class
    private Enquiry buildEnquiry(Cursor cursor) throws JSONException {
        int contentColumnIndex = cursor.getColumnIndex(content.getColumnName());
        Enquiry enquiry = new Enquiry(cursor.getString(contentColumnIndex));
        for (Database.EnquiryTableColumn column : Database.EnquiryTableColumn.values()) {
            final int columnIndex = cursor.getColumnIndex(column.getColumnName());

            if (columnIndex < 0 || column.equals(content)) {
                continue;
            } else if (column.getPrimitiveType().equals(Boolean.class)) {
                enquiry.put(column.getColumnName(), cursor.getInt(columnIndex) == 1);
            } else {
                enquiry.put(column.getColumnName(), cursor.getString(columnIndex));
            }
        }

        return enquiry;
    }

    public Enquiry get(String enquiryId) throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * from enquiry where id = ?", new String[]{enquiryId});
        if (cursor.moveToNext()) {
            return new Enquiry(cursor);
        } else {
            throw new NullPointerException(enquiryId);  //  I don't think it's cool to throw NullPointerExceptions - love John
        }
    }

    public List<Enquiry> getAllWithInternalIds(List<String> ids) {
        @Cleanup Cursor cursor = session.rawQuery(buildSelectAllQuery(ids), null);
        try {
            return toEnquiries(cursor);
        } catch (JSONException e) {
            return new ArrayList<Enquiry>();
        }
    }

    @Override
    public List<Enquiry> getRecordsForFirstPage() throws JSONException {
        String sql = String.format(
                "SELECT enquiry_json, synced FROM enquiry WHERE created_by ='%s' ORDER BY id LIMIT %d",
                userName, ViewAllChildrenPaginatedScrollListener.FIRST_PAGE);
        @Cleanup Cursor cursor = session.rawQuery(sql, null);
        return toEnquiries(cursor);
    }

    @Override
    public List<Enquiry> getRecordsBetween(int fromPageNumber, int pageNumber) throws JSONException {
        String sql = String.format(
                "SELECT enquiry_json, synced FROM enquiry WHERE created_by='%s' ORDER BY id LIMIT %d OFFSET %d",
                userName, pageNumber - fromPageNumber, pageNumber);
        Log.d("QUERY LIMIT", sql);
        @Cleanup Cursor cursor = session.rawQuery(sql, null);
        return toEnquiries(cursor);
    }

    private String buildSelectAllQuery(List<String> ids) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * from enquiry where _id in (");
        for(int i = 0; i < ids.size(); i++) {
            queryBuilder.append("'" + ids.get(i) + "'");
            if(i < ids.size() - 1) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(")");
        return queryBuilder.toString();
    }
}
