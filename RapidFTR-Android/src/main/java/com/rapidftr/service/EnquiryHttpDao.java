package com.rapidftr.service;

import com.rapidftr.model.Enquiry;
import org.json.JSONException;

public class EnquiryHttpDao {

    private final String apiRoot;
    private final JsonClient jsonClient;

    public EnquiryHttpDao(JsonClient jsonClient, String apiRoot) {
        this.jsonClient = jsonClient;
        this.apiRoot = apiRoot;
    }

    public Enquiry getEnquiry(String id) throws JSONException, ApiException {
        String enquiryJSON = jsonClient.get(apiRoot + "/api/enquiries/" + id);
        return new Enquiry(enquiryJSON);
    }

    public void postEnquiry(Enquiry enquiry) throws ApiException, JSONException {
        final String id = (String) enquiry.get("id");
        String uri = apiRoot + "/api/enquiries/" + id;
        jsonClient.put(uri, enquiry.getJsonString());
    }

}
