package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import static com.rapidftr.database.Database.EnquiryTableColumn.criteria;
import static com.rapidftr.database.Database.EnquiryTableColumn.enquirer_name;


public class Enquiry extends BaseModel {

    public Enquiry() throws JSONException {
        super();
        this.setUniqueId(createUniqueId());
    }

    public Enquiry(String createdBy, String reporterName, JSONObject criteria) throws JSONException {
        this.setCreatedBy(createdBy);
        this.setEnquirerName(reporterName);
        this.setCriteria(criteria);
        this.setUniqueId(createUniqueId());
        this.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
    }

    public Enquiry(Cursor cursor) throws JSONException {
        for(Database.EnquiryTableColumn column : Database.EnquiryTableColumn.values()) {
            final int idColumnIndex = cursor.getColumnIndex(column.getColumnName());
            if(idColumnIndex < 0) {
                throw new IllegalArgumentException("Column " + column.getColumnName() + " does not exist");
            }
            if(column.getPrimitiveType().equals(Boolean.class)) {
                this.put(column.getColumnName(), cursor.getInt(idColumnIndex) == 1);
            } else {
                this.put(column.getColumnName(), cursor.getString(idColumnIndex));
            }
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

    private void setColumn(Database.EnquiryTableColumn column, String value) throws JSONException {
        put(column.getColumnName(), value);
    }

    public String getEnquirerName() throws JSONException {
        return getString(enquirer_name.getColumnName());
    }

    public JSONObject getCriteria() throws JSONException {
        return new JSONObject(getString(criteria.getColumnName()));
    }

    public boolean isValid() {
        String enquirerName = null;
        try {
            enquirerName = getEnquirerName();
        } catch (JSONException e) {
            return false;
        }
        return null != enquirerName && !"".equals(enquirerName);
    }
}
