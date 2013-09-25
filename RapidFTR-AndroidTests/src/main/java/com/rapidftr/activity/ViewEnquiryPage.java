package com.rapidftr.activity;

import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.activity.pages.EnquiryPage;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;
import static junit.framework.Assert.assertTrue;

public class ViewEnquiryPage extends EnquiryPage {

    public ViewEnquiryPage(Solo solo) {
        super(solo);
    }

    public void navigateToPage(Enquiry enquiry) throws JSONException {
        solo.clickOnText("Enquiry");
        solo.clickOnText("View All");
        solo.clickOnText(enquiry.getEnquirerName());
    }

    public void validateData(Enquiry enquiry) throws JSONException {
        assertTrue(solo.searchText(enquiry.getShortId()));
        assertTrue(solo.searchEditText(enquiry.getEnquirerName()));
        assertTrue(solo.searchText("Edit"));
    }

}
