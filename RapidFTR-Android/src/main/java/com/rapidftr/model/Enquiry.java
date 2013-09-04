package com.rapidftr.model;

import org.json.JSONException;

public class Enquiry extends BaseModel {

    public Enquiry() throws JSONException {
        super();
        this.setUniqueId(createUniqueId());
    }

    public Enquiry(String id, String createdBy, String enquiryDetails) throws JSONException {
        super(id, createdBy, enquiryDetails);
    }
}
