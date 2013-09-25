package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import static com.rapidftr.database.Database.EnquiryTableColumn.criteria;
import static com.rapidftr.database.Database.EnquiryTableColumn.enquirer_name;
import static com.rapidftr.database.Database.EnquiryTableColumn.owner;


public class Enquiry extends BaseModel {

    public Enquiry() throws JSONException {
        super();
    }

    public Enquiry(String createdBy, String reporterName, JSONObject criteria) throws JSONException {
        this.setOwner(createdBy);
        this.setEnquirerName(reporterName);
        this.setCriteria(criteria);
        this.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
    }

    public Enquiry(Cursor cursor) throws JSONException { // TODO fix me - inflate with all columns
        for(Database.EnquiryTableColumn column : Database.EnquiryTableColumn.values()) {
            final int idColumnIndex = cursor.getColumnIndex(column.getColumnName());
            this.put(column.getColumnName(), cursor.getString(idColumnIndex));
        }
    }

    public Enquiry(String enquiryJSON) throws JSONException {
        super(enquiryJSON);
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
