package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import com.rapidftr.utils.RapidFtrDateTime;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;

public class EnquirySyncService implements SyncService<Enquiry> {

    private final RapidFtrApplication context;
    private final EnquiryHttpDao enquiryHttpDao;
    private final FluentRequest fluentRequest;

    @Inject
    public EnquirySyncService(RapidFtrApplication context, EnquiryHttpDao enquiryHttpDao, FluentRequest fluentRequest) {
        this.context = context;
        this.enquiryHttpDao = enquiryHttpDao;
        this.fluentRequest = fluentRequest;
    }

    @Override
    public Enquiry sync(Enquiry enquiry, User currentUser) throws IOException, JSONException {
        fluentRequest.path(getSyncPath(enquiry, currentUser)).context(context).param("enquiry", enquiry.values().toString());
        FluentResponse response;
        try {
            response = enquiry.isNew() ? fluentRequest.postWithMultipart() : fluentRequest.put();
        } catch (IOException e) {
            enquiry.setSynced(false);
            throw new SyncFailedException(e.getMessage());
        }
        try {
            if (response != null && response.isSuccess()) {
                String source = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
                enquiry = new Enquiry(source);
                enquiry.setSynced(true);
                enquiry.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
                return enquiry;
            }
        } catch (Exception e) {
            enquiry.setSynced(false);
        }
        return enquiry;
    }

    public String getSyncPath(Enquiry enquiry, User currentUser) throws JSONException {
        if (currentUser.isVerified()) {
            return enquiry.isNew() ? "/api/enquiries" : String.format("/api/enquiries/%s", enquiry.get(internal_id.getColumnName()));
        }
        return "";
    }

    @Override
    public Enquiry getRecord(String url) throws IOException, JSONException, HttpException {
        return enquiryHttpDao.getEnquiry(url);
    }

    @Override
    public List<String> getIdsToDownload() throws IOException, JSONException, HttpException {
        long lastUpdateMillis = context.getSharedPreferences().getLong("LAST_ENQUIRY_SYNC", 0);  // Default value is currently epoch
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return enquiryHttpDao.getIdsOfUpdated(lastUpdate);
    }

    @Override
    public void setMedia(Enquiry enquiry) throws IOException, JSONException {
        // do nothing
    }
}
