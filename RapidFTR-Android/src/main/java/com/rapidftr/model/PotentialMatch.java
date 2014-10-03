package com.rapidftr.model;

import com.google.common.base.Predicate;
import org.json.JSONException;

public class PotentialMatch extends BaseModel {
    private static final String ENQUIRY_ID_FIELD = "enquiry_id";
    private static final String CHILD_ID_FIELD = "child_id";
    private static final String CONFIRMED_FIELD = "confirmed";
    public static final String DELETED_FIELD = "deleted";

    public PotentialMatch(String jsonString) throws JSONException {
        super(jsonString);
    }

    public PotentialMatch(String enquiryId, String childId, String uniqueIdentifier) {
        this.put(ENQUIRY_ID_FIELD, enquiryId);
        this.put(CHILD_ID_FIELD, childId);
        this.put(FIELD_INTERNAL_ID, uniqueIdentifier);
    }

    public PotentialMatch(String enquiryId, String childId, String uniqueIdentifier, Boolean isConfirmed) {
        this(enquiryId, childId, uniqueIdentifier);
        this.put(CONFIRMED_FIELD, isConfirmed.toString());
    }

    public String getChildId() {
        return getString(CHILD_ID_FIELD);
    }

    public String getEnquiryId() {
        return getString(ENQUIRY_ID_FIELD);
    }

    public String getUniqueId() {
        return getString(FIELD_INTERNAL_ID);
    }

    public String getRevision() {
        return getString(FIELD_REVISION_ID);
    }

    @Override
    public String getApiPath() {
        return "/api/potential_matches";
    }

    @Override
    public String getApiParameter() {
        return "potential_match";
    }

    public Boolean isConfirmed() {
        return Boolean.valueOf(getString(CONFIRMED_FIELD));
    }

    public Boolean isDeleted() {
        return Boolean.valueOf(getString(DELETED_FIELD, "false"));
    }

    public static class FilterByConfirmationStatus implements Predicate<PotentialMatch> {
        private Boolean keepConfirmedMatches;

        public FilterByConfirmationStatus(Boolean keepConfirmedMatches) {
            this.keepConfirmedMatches = keepConfirmedMatches;
        }

        @Override
        public boolean apply(PotentialMatch potentialMatch) {
            return potentialMatch.isConfirmed() == keepConfirmedMatches;
        }
    }
}
