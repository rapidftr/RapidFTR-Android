package com.rapidftr.service;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;


public class EnquirySyncService implements SyncService<Enquiry> {

    public static final String ENQUIRIES_API_PATH = "/api/enquiries";
    public static final String ENQUIRIES_API_PARAMETER = "enquiry";

    private EntityHttpDao<Enquiry> enquiryHttpDao;
    private final EnquiryRepository enquiryRepository;
    private final SharedPreferences sharedPreferences;

    private static final int NOTIFICATION_ID = 1021;
    private MediaSyncHelper mediaSyncHelper;

    @Inject
    public EnquirySyncService(RapidFtrApplication rapidFtrApplication, EntityHttpDao<Enquiry> enquiryHttpDao, EnquiryRepository enquiryRepository) {
        this.sharedPreferences = rapidFtrApplication.getSharedPreferences();
        this.enquiryHttpDao = enquiryHttpDao;
        this.enquiryRepository = enquiryRepository;
        this.mediaSyncHelper = new MediaSyncHelper(enquiryHttpDao, rapidFtrApplication);
    }

    @Override
    public Enquiry sync(Enquiry record, User currentUser) throws IOException, JSONException, HttpException {
        String syncPath = getSyncPath(record);
        GenericSyncService<Enquiry> syncService = new GenericSyncService<Enquiry>(mediaSyncHelper, enquiryHttpDao, enquiryRepository);
        return syncService.sync(record, syncPath);
    }

    @Override
    public Enquiry getRecord(String url) throws IOException, JSONException, HttpException {
        Enquiry enquiry = enquiryHttpDao.get(url);
        GenericSyncService.setAttributes(enquiry);
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
        mediaSyncHelper.setPhoto(enquiry);
        mediaSyncHelper.setAudio(enquiry);
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public String getNotificationTitle() {
        return RapidFtrApplication.getApplicationInstance().getString(R.string.enquires_sync_title);
    }

    @Override
    public void setLastSyncedAt(Enquiry enquiry, boolean isLastRecord) {
        Long lastSynced = null;

        if (isLastRecord) {
            lastSynced = System.currentTimeMillis();
        } else {
            lastSynced = enquiry.lastUpdatedAtInMillis();
        }

        if (lastSynced == null) {
            return;
        }

        RapidFtrApplication.getApplicationInstance()
                .getSharedPreferences()
                .edit()
                .putLong(RapidFtrApplication.LAST_ENQUIRY_SYNC, lastSynced)
                .commit();
    }

    public String getSyncPath(Enquiry enquiry) throws JSONException {
        return enquiry.isNew() ? ENQUIRIES_API_PATH:
                new StringBuilder(ENQUIRIES_API_PATH)
                        .append("/").append(enquiry.get(internal_id.getColumnName())).toString();
    }
}
