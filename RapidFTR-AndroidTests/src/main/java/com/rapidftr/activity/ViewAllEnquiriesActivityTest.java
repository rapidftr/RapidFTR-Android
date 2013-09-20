package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewAllEnquiriesActivityTest  extends BaseActivityIntegrationTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
    }

    public void testListAllEnquiries() throws JSONException {
        EnquiryRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(EnquiryRepository.class);
        Enquiry rajni = new Enquiry("FIELD WORKER 1", "Rajni", new JSONObject("{name:NAME}"));
        repository.createOrUpdate(rajni);
        Enquiry kamal = new Enquiry("FIELD WORKER 2", "Kamal", new JSONObject("{name:NAME}"));
        repository.createOrUpdate(kamal);
        viewAllEnquiriesPage.navigateToPage();
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(rajni));
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(kamal));
    }

}
