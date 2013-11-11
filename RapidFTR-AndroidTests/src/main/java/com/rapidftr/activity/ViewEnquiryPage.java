package com.rapidftr.activity;

import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.activity.pages.EnquiryPage;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;

import static junit.framework.Assert.assertTrue;

public class ViewEnquiryPage extends EnquiryPage {

    public ViewEnquiryPage(Solo solo) {
        super(solo);
    }

    public void navigateToPage(String enquirerName) throws JSONException {
        solo.clickOnText("Enquiry");
        solo.clickOnText("View All");
        solo.clickOnText(enquirerName);
    }

    public void validateData(Enquiry enquiry) throws JSONException {
        assertTrue(solo.searchText(enquiry.getShortId()));
        assertTrue(solo.searchEditText(enquiry.getEnquirerName()));
        assertTrue(solo.searchText("Edit"));
    }

    public boolean isChildPresent(String childUniqueId) throws JSONException {
        return  solo.searchText(childUniqueId);
    }
}
