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
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(apiRoot + "/api/enquiries/" + enquiry.get("_id"))
                .param("enquiry", enquiry.getJsonString())
                .put()
                .ensureSuccess();
        String json = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        return new Enquiry(json);
    }

    public List<String> getIdsOfUpdated(DateTime lastUpdate) throws IOException, JSONException, HttpException {
        String utcString = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC).print(lastUpdate) + "UTC";
        final FluentResponse fluentResponse = http().context(RapidFtrApplication.getApplicationInstance())
                .host(apiRoot + "/api/enquiries")
                .param("updated_after", URLEncoder.encode(utcString, "UTF-8"))
                .get()
                .ensureSuccess();
        String json = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        JSONArray jsonArray = new JSONArray(json);
        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            urls.add(jsonArray.getJSONObject(i).getString("location"));
        }
        return urls;
    }

    public Enquiry create(Enquiry enquiry) throws IOException, HttpException, JSONException {
        FluentResponse fluentResponse = http()
                .context(RapidFtrApplication.getApplicationInstance())
                .host(apiRoot + "/api/enquiries")
                .param("enquiry", enquiry.getJsonString())
                .post()
                .ensureSuccess();
        String json = CharStreams.toString(new InputStreamReader(fluentResponse.getEntity().getContent()));
        return new Enquiry(json);
    }
}
