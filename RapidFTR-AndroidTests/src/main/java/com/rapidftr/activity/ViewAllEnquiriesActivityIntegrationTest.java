package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewAllEnquiriesActivityIntegrationTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
    }

    public void testDisplayAllEnquiries() throws Exception {
        EnquiryRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(EnquiryRepository.class);
        Enquiry enquiry1 = new Enquiry("CREATEDBY", "Enq1Reportername", new JSONObject("{name:NAME}"));
        Enquiry enquiry2 = new Enquiry("CREATEDBY", "Enq2Reportername", new JSONObject("{name:NAME}"));
        repository.createOrUpdate(enquiry1);
        repository.createOrUpdate(enquiry2);
        viewAllEnquiriesPage.navigateToPage();
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(enquiry1));
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(enquiry2));
    }


}
