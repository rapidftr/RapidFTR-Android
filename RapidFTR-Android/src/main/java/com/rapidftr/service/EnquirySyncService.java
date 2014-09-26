package com.rapidftr.service;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.io.SyncFailedException;
import java.util.List;


public class EnquirySyncService implements SyncService<Enquiry> {

    public static final String ENQUIRIES_API_PATH = "/api/enquiries";
    public static final String ENQUIRIES_API_PARAMETER = "enquiry";

    private EntityHttpDao<Enquiry> enquiryHttpDao;
    private final EnquiryRepository enquiryRepository;
    private final SharedPreferences sharedPreferences;

    private static final int NOTIFICATION_ID = 1021;

    @Inject
    public EnquirySyncService(RapidFtrApplication rapidFtrApplication, EntityHttpDao<Enquiry> enquiryHttpDao, EnquiryRepository enquiryRepository) {
        this.sharedPreferences = rapidFtrApplication.getSharedPreferences();
        this.enquiryHttpDao = enquiryHttpDao;
        this.enquiryRepository = enquiryRepository;
    }

    @Override
    public Enquiry sync(Enquiry record, User currentUser) throws IOException, JSONException, HttpException {
        try {
            record = record.isNew() ? enquiryHttpDao.create(record) : enquiryHttpDao.update(record);
            record.setSynced(true);
            record.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
            record.remove(History.HISTORIES);
            enquiryRepository.createOrUpdateWithoutHistory(record);
        } catch (Exception exception) {
            record.setSynced(false);
            record.setLastUpdatedAt(null);
            enquiryRepository.createOrUpdateWithoutHistory(record);
            enquiryRepository.close();
            throw new SyncFailedException(exception.getMessage());
        }

        return record;
    }

    @Override
    public Enquiry getRecord(String url) throws IOException, JSONException, HttpException {
        Enquiry enquiry = enquiryHttpDao.get(url);
        enquiry.setSynced(true);
        enquiry.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
        enquiry.remove(Enquiry.FIELD_ATTACHMENTS);

        return enquiry;
    }


    @Override
    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        long lastUpdateMillis = sharedPreferences.getLong(RapidFtrApplication.LAST_ENQUIRY_SYNC, 0);  // Default value is currently epoch
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return enquiryHttpDao.getUpdatedResourceUrls(lastUpdate);
    }

    @Override
    public void setMedia(Enquiry enquiry) throws IOException, JSONException {
        // do nothing
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public String getNotificationTitle() {
        return RapidFtrApplication.getApplicationInstance().getString(R.string.enquires_sync_title);
    }


}
