package com.rapidftr.model;

import org.json.JSONException;

import static com.rapidftr.database.Database.ChildTableColumn.last_synced_at;

public class PotentialMatch extends BaseModel {
    private String childId;
    private String enquiryId;

    public PotentialMatch(String enquiryId, String childId) {
        this.enquiryId = enquiryId;
        this.childId = childId;
    }
    public void setLastSyncedAt(String lastSyncedAt) throws JSONException {
        put(last_synced_at.getColumnName(), lastSyncedAt);
    }

    public String getChildId() {
        return childId;
    }

    public String getEnquiryId() {
        return enquiryId;
    }
}
