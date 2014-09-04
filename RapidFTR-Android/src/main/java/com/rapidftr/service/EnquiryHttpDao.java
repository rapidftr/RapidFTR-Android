package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.utils.http.FluentRequest.http;

public class EnquiryHttpDao {

    public static final String ENQUIRIES_API_PATH = "/api/enquiries";
    private static final String ENQUIRY_FORM_PARAMETER = "enquiry";
    private static final String UPDATED_AFTER_FORM_PARAMETER = "updated_after";
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String LOCATION_ATTRIBUTE = "location";
    private static final String CHARACTER_SET = "UTF-8";

    private final String apiRoot;

    @Inject
    public EnquiryHttpDao() {
        this(RapidFtrApplication.getApplicationInstance().getCurrentUser().getServerUrl());
    }

    public EnquiryHttpDao(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    public Enquiry get(String url) throws JSONException, IOException, HttpException {
        final FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(url)
                .get()
                .ensureSuccess();
        String enquiryJSON = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        return new Enquiry(enquiryJSON);
    }

    public Enquiry update(Enquiry enquiry) throws JSONException, IOException, HttpException {
        String url = new StringBuilder(apiRoot).append(ENQUIRIES_API_PATH).append("/").append(enquiry.get(Enquiry.FIELD_INTERNAL_ID)).toString();
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(url)
                .param(ENQUIRY_FORM_PARAMETER, enquiry.values().toString())
                .putWithMultiPart()
                .ensureSuccess();
        String json = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        return new Enquiry(json);
    }

    public List<String> getIdsOfUpdated(DateTime lastUpdate) throws IOException, JSONException, HttpException {
        String utcString = new StringBuilder(DateTimeFormat.forPattern(DATE_PATTERN).withZone(DateTimeZone.UTC).print(lastUpdate)).append("UTC").toString();
        final FluentResponse fluentResponse = http().context(RapidFtrApplication.getApplicationInstance())
                .host(new StringBuilder(apiRoot).append(ENQUIRIES_API_PATH).toString())
                .param(UPDATED_AFTER_FORM_PARAMETER, URLEncoder.encode(utcString, CHARACTER_SET))
                .get()
                .ensureSuccess();
        String json = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        JSONArray jsonArray = new JSONArray(json);
        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            urls.add(jsonArray.getJSONObject(i).getString(LOCATION_ATTRIBUTE));
        }
        return urls;
    }

    public Enquiry create(Enquiry enquiry) throws IOException, HttpException, JSONException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(new StringBuilder(apiRoot).append(ENQUIRIES_API_PATH).toString())
                .param(ENQUIRY_FORM_PARAMETER, enquiry.getJsonString())
                .post()
                .ensureSuccess();
        String json = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        return new Enquiry(json);
    }
}
