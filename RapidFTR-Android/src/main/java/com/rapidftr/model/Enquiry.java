package com.rapidftr.model;

import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import lombok.Cleanup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Inject
    public List<Child> getPotentialMatches(ChildRepository childRepository) throws JSONException {
//       return new String[]{"8dab5561-6a3e-450a-89da-a0660e58fdc5","dc8fb3bf-420f-468e-ad3c-3790732cd186"};
//        String matchingChildIds = getString(potential_matches.getColumnName());
        JSONArray ja = new JSONArray(matchingChildIds());
        List<String> matchingChildIds = new ArrayList<String>();

        for(int i=0; i<ja.length(); i++){
            matchingChildIds.add((String)ja.get(i));
        }

        Injector injector = RapidFtrApplication.getApplicationInstance().getInjector();
//        @Cleanup ChildRepository repo = injector.getInstance(ChildRepository.class);
//        return repo.getChildrenByIds(new ArrayList<String>(x));
        return childRepository.getChildrenByIds(new ArrayList<String>(matchingChildIds));
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
        return getString(potential_matches.getColumnName());  //To change body of created methods use File | Settings | File Templates.
    }
}
