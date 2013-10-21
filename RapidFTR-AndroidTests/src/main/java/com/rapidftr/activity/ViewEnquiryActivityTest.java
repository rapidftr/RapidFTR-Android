package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewEnquiryActivityTest extends BaseActivityIntegrationTest {
    EnquiryRepository repository;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
        repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(EnquiryRepository.class);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShowEnquiry() throws JSONException {
        Enquiry enquiry = new Enquiry("CREATEDBY", "Enq1Reportername", new JSONObject("{enquirer_name:Enq1Reportername}"));
        enquiry.put("f9e9ad8c", "01/01/01"); // Hardcoded key till enquiry form sections can be synced
        repository.createOrUpdate(enquiry);
        enquiry = repository.get(enquiry.getUniqueId());
        viewEnquiryPage.navigateToPage(enquiry.getEnquirerName());
        viewEnquiryPage.validateData(enquiry);
    }
}
