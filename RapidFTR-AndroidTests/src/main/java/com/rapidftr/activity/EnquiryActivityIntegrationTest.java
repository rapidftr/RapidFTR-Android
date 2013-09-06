package com.rapidftr.activity;

public class EnquiryActivityIntegrationTest extends BaseActivityIntegrationTest {

    public void shouldBeInEnquiryActivity() {
        solo.assertCurrentActivity("", EnquiryActivity.class);
    }
}
