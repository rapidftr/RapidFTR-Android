package com.rapidftr.service;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class EnquirySyncService implements SyncService<Enquiry> {

    private final SharedPreferences sharedPreferences;
    private final EnquiryHttpDao enquiryHttpDao;

    @Inject
    public EnquirySyncService(SharedPreferences sharedPreferences, EnquiryHttpDao enquiryHttpDao) {
        this.sharedPreferences = sharedPreferences;
        this.enquiryHttpDao = enquiryHttpDao;
    }

    @Override
    public Enquiry sync(Enquiry record, User currentUser) throws IOException, JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enquiry getRecord(String url) throws IOException, JSONException, HttpException {
        return enquiryHttpDao.getEnquiry(url);
    }

    @Override
    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        long lastUpdateMillis = sharedPreferences.getLong(RapidFtrApplication.LAST_ENQUIRY_SYNC, 0);  // Default value is currently epoch
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return enquiryHttpDao.getIdsOfUpdated(lastUpdate);
    }

    @Override
    public void setMedia(Enquiry enquiry) throws IOException, JSONException {
        // do nothing
    }
}
