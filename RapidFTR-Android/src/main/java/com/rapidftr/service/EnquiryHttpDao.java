package com.rapidftr.service;

import com.rapidftr.model.Enquiry;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

public class EnquiryHttpDao {

    private final String apiRoot;
    private final JsonClient jsonClient;

    public EnquiryHttpDao(JsonClient jsonClient, String apiRoot) {
        this.jsonClient = jsonClient;
        this.apiRoot = apiRoot;
    }

    public Enquiry getEnquiry(String url) throws JSONException, ApiException {
        String enquiryJSON = jsonClient.get(url);
        return new Enquiry(enquiryJSON);
    }

    public void updateEnquiry(Enquiry enquiry) throws ApiException, JSONException {
        final String id = (String) enquiry.get("id");
        String uri = apiRoot + "/api/enquiries/" + id;
        jsonClient.put(uri, enquiry.getJsonString());
    }

    public List<String> getIdsOfUpdated(DateTime lastUpdate) throws ApiException, JSONException {
        String utcString = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC).print(lastUpdate) + "UTC";
        System.out.println(utcString);
        MultivaluedMap<String, String> multivaluedMap = new MultivaluedMapImpl();
        multivaluedMap.putSingle("updated_after", utcString);
        String json = jsonClient.get(apiRoot + "/api/enquiries", multivaluedMap);
        JSONArray jsonArray = new JSONArray(json);
        List<String> urls = new ArrayList<String>();
        for(int i = 0; i < jsonArray.length(); i++) {
            urls.add(jsonArray.getJSONObject(i).getString("location"));
        }
        return urls;
    }
}
