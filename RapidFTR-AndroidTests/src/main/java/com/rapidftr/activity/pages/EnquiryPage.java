package com.rapidftr.activity.pages;

import com.jayway.android.robotium.solo.Solo;

import java.util.Arrays;
import java.util.List;

public class EnquiryPage {
    public Solo solo;
    List automationFormData = Arrays.asList("Automation TextField name", "Automation TextArea location");

    public EnquiryPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToEnquiryActivity() {
        solo.clickOnText("Enquiry");
        solo.waitForText("Enquiry details");
    }
}
