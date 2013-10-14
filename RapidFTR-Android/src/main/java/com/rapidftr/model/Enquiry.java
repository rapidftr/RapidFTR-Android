package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.database.Database;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public Enquiry(Cursor cursor) throws JSONException {
        int index = cursor.getColumnIndex(Database.EnquiryTableColumn.criteria.getColumnName());
        buildFromContent(cursor.getString(index));
    }

    private void buildFromContent(String string) throws JSONException {
        JSONObject contents = new JSONObject(string);
        Iterator<String> keys = contents.keys();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            this.put(key, contents.get(key));
        }
    }

    public String getOwner() throws JSONException {
        return getString(owner.getColumnName());
    }

    public List<Child> getPotentialMatches(ChildRepository childRepository) throws JSONException {
        try{
            JSONArray matchingChildIdArray = new JSONArray(matchingChildIds());
            List<String> matchingChildList = getListOfMatchingChildsFrom(matchingChildIdArray);

            return childRepository.getChildrenByIds(new ArrayList<String>(matchingChildList));
        }catch (JSONException exception){
            return new ArrayList<Child>();
        }
    }

    private List<String> getListOfMatchingChildsFrom(JSONArray matchingChildIdArray) throws JSONException {
        List<String> matchingChildList = new ArrayList<String>();

        for(int i=0; i<matchingChildIdArray.length(); i++){
            matchingChildList.add((String) matchingChildIdArray.get(i));
        }
        return matchingChildList;
    }

    private void setColumn(Database.EnquiryTableColumn column, String value) throws JSONException {
        put(column.getColumnName(), value);
    }

    public String getEnquirerName() throws JSONException {
        return getString(enquirer_name.getColumnName());
    }

    public void setEnquirerName(String reporterName) throws JSONException {
        this.setColumn(enquirer_name, reporterName);
    }

    public JSONObject getCriteria() throws JSONException {
        return new JSONObject(getString(criteria.getColumnName()));
    }

    public void setCriteria(JSONObject criteria) throws JSONException {
        this.setColumn(Database.EnquiryTableColumn.criteria, criteria.toString());
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

    public String matchingChildIds() throws JSONException {
        String ids = getString(potential_matches.getColumnName());
        if(ids == null) throw new JSONException("No key" + potential_matches.getColumnName());
        return ids;
    }
}
