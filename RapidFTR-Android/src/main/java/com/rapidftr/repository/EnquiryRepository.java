package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Enquiry;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.database.Database.EnquiryTableColumn.*;

public class EnquiryRepository implements Closeable {

    private final String user;
    private final DatabaseSession session;

    @Inject
    public EnquiryRepository(@Named("USER_NAME")String user, DatabaseSession session) {
        this.user = user;
        this.session = session;
    }

    public void createOrUpdate(Enquiry enquiry) throws JSONException {
        ContentValues enquiryValues = new ContentValues();

        enquiryValues.put(owner.getColumnName(), enquiry.getOwner());
        enquiryValues.put(enquirer_name.getColumnName(), enquiry.getEnquirerName());
        enquiryValues.put(criteria.getColumnName(), enquiry.toString());
        enquiryValues.put(created_at.getColumnName(), enquiry.getCreatedAt());
        enquiryValues.put(id.getColumnName(), enquiry.getUniqueId());
        long id =  session.replace(Database.enquiry.getTableName(), null, enquiryValues);
        if(id <= 0) throw new IllegalArgumentException();
        //TODO : Better error handling
    }

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

    public Enquiry get(String enquiryId) throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * from enquiry where id = ?", new String[]{enquiryId});
        if (cursor.moveToNext()){
            return buildEnquiry(cursor);
        }else{
            throw new NullPointerException(enquiryId);
        }
    }
}
