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
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.database.Database.ChildTableColumn.internal_rev;
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
        enquiryValues.put(created_by.getColumnName(), enquiry.getOwner());

        enquiryValues.put(owner.getColumnName(), enquiry.getOwner());
        enquiryValues.put(id.getColumnName(), enquiry.getUniqueId());
        enquiryValues.put(name.getColumnName(), enquiry.getName());
        enquiryValues.put(content.getColumnName(), enquiry.toString());
        enquiryValues.put(synced.getColumnName(), enquiry.isSynced());
        enquiryValues.put(created_at.getColumnName(), enquiry.getCreatedAt());
        enquiryValues.put(internal_id.getColumnName(), enquiry.optString("_id"));
        long id =  session.replace(Database.enquiry.getTableName(), null, enquiryValues);
        if(id <= 0) throw new IllegalArgumentException();
        //TODO : Better error handling
    }

    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM enquiry WHERE enquiry_owner = ?", new String[]{user});
        return cursor.moveToNext() ? cursor.getInt(0) : 0;
    }

    @Override
    public void close() throws IOException {
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllEnquirerNames() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT name FROM enquiry WHERE enquiry_owner = ?", new String[] {user});
        List<String> enquirerNames = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            enquirerNames.add(cursor.getString(1));
            cursor.moveToNext();
        }
        return enquirerNames;
    }
}
