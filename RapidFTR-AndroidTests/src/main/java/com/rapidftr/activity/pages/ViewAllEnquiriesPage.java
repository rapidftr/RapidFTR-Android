package com.rapidftr.activity.pages;

import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;

public class ViewAllEnquiriesPage {
    public Solo solo;

    public ViewAllEnquiriesPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToPage() {
        solo.clickOnText("Enquiry");
        solo.sleep(1000);
        solo.clickOnText("View All");
        solo.sleep(1000);
    }

    public boolean isEnquiryPresent(Enquiry enquiry) throws JSONException {
        return solo.searchText(enquiry.getEnquirerName());
    }
}
