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
        enquiryValues.put(reporter_name.getColumnName(), enquiry.getReporterName());
        enquiryValues.put(reporter_details.getColumnName(), enquiry.getReporterDetails().toString());
        enquiryValues.put(criteria.getColumnName(), enquiry.getCriteria().toString());
        enquiryValues.put(created_at.getColumnName(), enquiry.getCreatedAt());
        enquiryValues.put(id.getColumnName(), enquiry.getUniqueId());

        long id =  session.replace(Database.enquiry.getTableName(), null, enquiryValues);
        if(id <= 0) throw new IllegalArgumentException();
        //TODO : Better error handling
    }

    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM enquiry WHERE created_by = ?", new String[]{user});
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
        @Cleanup Cursor cursor = session.rawQuery("SELECT name FROM enquiry WHERE created_by = ?", new String[] {user});
        List<String> enquirerNames = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            enquirerNames.add(cursor.getString(1));
            cursor.moveToNext();
        }
        return enquirerNames;
    }
}
