package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
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

    public void testDisplayAllEnquiries() throws JSONException {
        EnquiryRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(EnquiryRepository.class);
        Enquiry enquiry1 = new Enquiry("CREATEDBY", "Enq1Reportername", new JSONObject("{name:NAME}"));
        Enquiry enquiry2 = new Enquiry("CREATEDBY", "Enq2Reportername", new JSONObject("{name:NAME}"));
        repository.createOrUpdate(enquiry1);
        repository.createOrUpdate(enquiry2);
        viewAllEnquiriesPage.navigateToPage();
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(enquiry1));
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(enquiry2));
    }

    public void testClickOnChildShouldShowViewPage() throws JSONException {

        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        Child child1 = new Child(getAlphaNumeric(4), "admin", "{\"name\":\"Test1\"}");
        repository.createOrUpdate(child1);
        Child child2 = new Child(getAlphaNumeric(6), "admin", "{\"name\":\"Test2\"}");
        repository.createOrUpdate(child2);
        viewAllChildrenPage.navigateToViewAllTab();
        viewAllChildrenPage.clickChild(child1.getUniqueId());
        viewAllChildrenPage.verifyChildDetails(child1);
    }





}
