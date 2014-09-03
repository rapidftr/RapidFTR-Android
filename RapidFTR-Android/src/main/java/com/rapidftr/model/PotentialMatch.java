package com.rapidftr.model;

public class PotentialMatch extends BaseModel {
    private String childId;
    private String uniqueIdentifier;
    private String enquiryId;
    private String revision;

    public PotentialMatch(String enquiryId, String childId, String uniqueIdentifier) {
        this.enquiryId = enquiryId;
        this.childId = childId;
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getChildId() {
        return childId;
    }

    public String getEnquiryId() {
        return enquiryId;
    }

    public String getUniqueId() {
        return uniqueIdentifier;
    }

    public String getRevision() {
        return revision;
    }
}
