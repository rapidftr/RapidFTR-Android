package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Enquiry;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.database.Database.BooleanColumn.falseValue;
import static com.rapidftr.database.Database.EnquiryTableColumn.created_at;
import static com.rapidftr.database.Database.EnquiryTableColumn.criteria;
import static com.rapidftr.database.Database.EnquiryTableColumn.enquirer_name;
import static com.rapidftr.database.Database.EnquiryTableColumn.id;
import static com.rapidftr.database.Database.EnquiryTableColumn.internal_id;
import static com.rapidftr.database.Database.EnquiryTableColumn.internal_rev;
import static com.rapidftr.database.Database.EnquiryTableColumn.created_by;
import static com.rapidftr.database.Database.EnquiryTableColumn.synced;
import static com.rapidftr.database.Database.EnquiryTableColumn.unique_identifier;
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
    public void createOrUpdate(Enquiry enquiry) throws JSONException {
        ContentValues enquiryValues = new ContentValues();

        enquiryValues.put(id.getColumnName(), enquiry.getUniqueId());
        enquiryValues.put(created_by.getColumnName(), enquiry.getCreatedBy());
        enquiryValues.put(enquirer_name.getColumnName(), enquiry.getEnquirerName());
        enquiryValues.put(criteria.getColumnName(), enquiry.getCriteria().toString());
        enquiryValues.put(created_at.getColumnName(), enquiry.getCreatedAt());
        enquiryValues.put(unique_identifier.getColumnName(), enquiry.getUniqueId());
        enquiryValues.put(synced.getColumnName(), enquiry.isSynced());
        enquiryValues.put(internal_id.getColumnName(), enquiry.optString(internal_id.getColumnName()));
        enquiryValues.put(internal_rev.getColumnName(), enquiry.optString(internal_rev.getColumnName()));
        long errorCode =  session.replace(Database.enquiry.getTableName(), null, enquiryValues);
        if(errorCode <= 0) throw new IllegalArgumentException(errorCode + "");
        //TODO : Better error handling
    }

    @Override
    public HashMap<String, String> getAllIdsAndRevs() throws JSONException {
        HashMap<String, String> idRevs = new HashMap<String, String>();
        @Cleanup Cursor cursor = session.rawQuery("SELECT "
                + internal_id.getColumnName() + ", "
                + internal_rev.getColumnName()
                + " FROM "+ enquiry.getTableName(), null);
        while(cursor.moveToNext()){
            idRevs.put(cursor.getString(0), cursor.getString(1));
        }
        return idRevs;
    }

    @Override
    public void update(Enquiry enquiry) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(enquirer_name.getColumnName(), enquiry.getEnquirerName());
        values.put(criteria.getColumnName(), enquiry.getCriteria().toString());
        values.put(created_by.getColumnName(), enquiry.getCreatedBy());
        values.put(synced.getColumnName(), enquiry.isSynced() ? 1 : 0);
        values.put(internal_id.getColumnName(), enquiry.getString(internal_id.getColumnName()));
        values.put(internal_rev.getColumnName(), enquiry.getString(internal_rev.getColumnName()));
        session.update(
                Database.enquiry.getTableName(),
                values,
                format("%s=?", id.getColumnName()),
                new String[]{enquiry.getUniqueId()});
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
    public Enquiry get(String id) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM enquiry", new String[]{});
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
}
