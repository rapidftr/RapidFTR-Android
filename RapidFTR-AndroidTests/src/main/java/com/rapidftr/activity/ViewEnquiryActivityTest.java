package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewEnquiryActivityTest extends BaseActivityIntegrationTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
    }

    public void testShowEnquiry() throws JSONException {
        EnquiryRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(EnquiryRepository.class);
        Enquiry enquiry = new Enquiry("CREATEDBY", "Enq1Reportername", new JSONObject("{enquirer_name:Enq1Reportername}"));
        repository.createOrUpdate(enquiry);
        enquiry = repository.get(enquiry.getUniqueId());
        viewEnquiryPage.navigateToPage(enquiry);
        viewEnquiryPage.validateData(enquiry);
    }
}
