package com.rapidftr.activity;

import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.FailedToSaveException;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewEnquiryActivityTest extends BaseActivityIntegrationTest {
    EnquiryRepository repository;
    ChildRepository childRepository;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
        repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(EnquiryRepository.class);
        childRepository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShowEnquiry() throws Exception {
        String enquiryJSON = "{ " +
                "\"enquirer_name\":\"Tom Cruise\", " +
                "\"name\":\"Matthew\"," +
                String.format("\"created_by\":\"%s\",", application.getCurrentUser().getUserName()) +
                "\"synced\" : \"false\"}";

        Enquiry enquiry = new Enquiry(enquiryJSON);
        repository.createOrUpdate(enquiry);
        viewEnquiryPage.navigateToPage(enquiry.getEnquirerName());
        viewEnquiryPage.validateData(enquiry);
    }

}
