package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Enquiry;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.database.Database.BooleanColumn.falseValue;
import static com.rapidftr.database.Database.EnquiryTableColumn.*;
import static com.rapidftr.database.Database.enquiry;
import static java.lang.String.format;

public class EnquiryRepository implements Closeable, Repository<Enquiry> {

    private final String user;
    private final DatabaseSession session;

    @Inject
    public EnquiryRepository(@Named("USER_NAME")String user, DatabaseSession session) {
        this.user = user;
        this.session = session;
    }

    @Override
    public void createOrUpdate(Enquiry enquiry) throws JSONException, FailedToSaveException {
        long errorCode = session.replace(Database.enquiry.getTableName(), null, getContentValuesFrom(enquiry));
        if(errorCode < 0)
            throw new FailedToSaveException("Failed to save enquiry.", errorCode);
    }

    protected ContentValues getContentValuesFrom(Enquiry enquiry) throws JSONException {
        ContentValues enquiryValues = new ContentValues();

        enquiryValues.put(id.getColumnName(), enquiry.getUniqueId());
        enquiryValues.put(created_by.getColumnName(), enquiry.getCreatedBy());
        enquiryValues.put(enquirer_name.getColumnName(), enquiry.getEnquirerName());
        enquiryValues.put(criteria.getColumnName(), enquiry.getCriteria().toString());
        enquiryValues.put(created_at.getColumnName(), enquiry.getCreatedAt());
        enquiryValues.put(potential_matches.getColumnName(), enquiry.getPotentialMatchingIds());
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
                + " FROM "+ enquiry.getTableName(), null);
        close();
        while(cursor.moveToNext()){
            idRevs.put(cursor.getString(0), cursor.getString(1));
        }
        return idRevs;
    }

    @Override
    public void update(Enquiry enquiry) throws JSONException {
        ContentValues values = getContentValuesFrom(enquiry);
        session.update(
                Database.enquiry.getTableName(),
                values,
                format("%s=?", id.getColumnName()),
                new String[]{enquiry.getUniqueId()});
        close();
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
        close();
        return toEnquiries(cursor);
    }

    @Override
    public boolean exists(String id) {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry WHERE id = ?", new String[]{id == null ? "" : id});
        close();
        return cursor.moveToNext() && cursor.getCount() > 0;
    }

    @Override
    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM enquiry", new String[]{});
        close();
        return cursor.moveToNext() ? cursor.getInt(0) : 0;
    }

    @Override
    public void close(){
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Enquiry> all() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry", new String[]{});
        close();
        return toEnquiries(cursor);
    }

    private List<Enquiry> toEnquiries(Cursor cursor) throws JSONException {
        List<Enquiry> enquiries = new ArrayList<Enquiry>();
        while (cursor.moveToNext()){
            enquiries.add(buildEnquiry(cursor));
        }
        return enquiries;
    }

    private Enquiry buildEnquiry(Cursor cursor) throws JSONException {
        return new Enquiry(cursor);
    }

    public Enquiry get(String enquiryId) throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * from enquiry where id = ?", new String[]{enquiryId});
        close();
        if (cursor.moveToNext()){
            return new Enquiry(cursor);
        }else{
            throw new NullPointerException(enquiryId);  //  I don't think it's cool to throw NullPointerExceptions - love John
        }
    }
}
