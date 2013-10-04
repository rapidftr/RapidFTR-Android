package com.rapidftr.service;

import android.content.SharedPreferences;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class EnquirySyncService implements SyncService<Enquiry> {

    private final SharedPreferences sharedPreferences;
    private final EnquiryHttpDao enquiryHttpDao;

    public EnquirySyncService(SharedPreferences sharedPreferences, EnquiryHttpDao enquiryHttpDao) {
        this.sharedPreferences = sharedPreferences;
        this.enquiryHttpDao = enquiryHttpDao;
    }

    @Override
    public Enquiry sync(Enquiry record, User currentUser) throws IOException, JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enquiry getRecord(String url) throws IOException, JSONException {
        return enquiryHttpDao.getEnquiry(url);
    }

    @Override
    public List<String> getIdsToDownload() throws IOException, JSONException {
        long lastUpdateMillis = sharedPreferences.getLong(RapidFtrApplication.LAST_ENQUIRY_SYNC, 0);  // Default value is currently epoch
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return enquiryHttpDao.getIdsOfUpdated(lastUpdate);
    }

    @Override
    public void setMedia(Enquiry enquiry) throws IOException, JSONException {
        // do nothing
    }
}
