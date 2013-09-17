package com.rapidftr.model;

import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import static com.rapidftr.database.Database.EnquiryTableColumn.*;


public class Enquiry extends BaseModel {

    public Enquiry() throws JSONException {
        super();
        this.setUniqueId(createUniqueId());
    }

    public Enquiry(String createdBy, String reporterName, JSONObject criteria) throws JSONException {
        this.setOwner(createdBy);
        this.setEnquirerName(reporterName);
        this.setCriteria(criteria);
        this.setUniqueId(createUniqueId());
        this.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
    }

    public void setEnquirerName(String reporterName) throws JSONException {
        this.setColumn(enquirer_name, reporterName);
    }


    public void setCriteria(JSONObject criteria) throws JSONException {
        this.setColumn(Database.EnquiryTableColumn.criteria, criteria.toString());
    }

    public String getOwner() throws JSONException {
        return getString(owner.getColumnName());
    }

    private void setColumn(Database.EnquiryTableColumn column, String value) throws JSONException {
        put(column.getColumnName(), value);
    }

    public String getEnquirerName() throws JSONException {
        return getString(enquirer_name.getColumnName());
    }

    public JSONObject getCriteria() throws JSONException {
        return new JSONObject(getString(criteria.getColumnName()));
    }
}
