package com.rapidftr.model;

public class PotentialMatch extends BaseModel {
    private Enquiry enquiry;
    private Child child;

    public PotentialMatch(Enquiry enquiry, Child child) {

        this.enquiry = enquiry;
        this.child = child;
    }

    public Child getChild() {
        return child;
    }

    public Enquiry getEnquiry() {
        return enquiry;
    }
}
