package com.rapidftr.activity;

import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.pages.ViewAllEnquiriesPage;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.task.SynchronisationAsyncTask;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

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

    public void testShowEnquiry() throws JSONException {
        Enquiry enquiry = new Enquiry("CREATEDBY", "Enq1Reportername", new JSONObject("{enquirer_name:Enq1Reportername}"));
        enquiry.put("f9e9ad8c", "01/01/01"); // Hardcoded key till enquiry form sections can be synced
        repository.createOrUpdate(enquiry);
        enquiry = repository.get(enquiry.getUniqueId());
        viewEnquiryPage.navigateToPage(enquiry.getEnquirerName());
        viewEnquiryPage.validateData(enquiry);
    }

    public void testShowMatchingChildRecords() throws JSONException {
        //given i have 2 children
//        Child child1 = new Child("id1", "user1", "{\"name\":\"child1\", \"sex\":\"female\"}");
//        childRepository.createOrUpdate(child1);
//        Child child2 = new Child("id2", "user1", "{\"name\":\"child2\", \"sex\":\"female\"}");
//        childRepository.createOrUpdate(child2);

        //childPage.navigateToRegisterPage();
        //childPage.enterChildName("child1");
        //childPage.save();
        childRepository.createOrUpdate(new Child("id1", "admin","{\"name\":\"Test1\", \"nationality\":\"Ugandan\"}"));
        childRepository.createOrUpdate(new Child("id2", "admin", "{\"name\":\"Test2\", \"nationality\":\"Ugandan\"}"));
        viewAllChildrenPage.navigateToViewAllTab();

        //when  i create an enquiry matching both
        Enquiry enquiry=new Enquiry("sam fisher", "some guy",  new JSONObject("{\"nationality\":\"Ugandan\"}"));
        repository.createOrUpdate(enquiry);

        //and sync all enquiries and children
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(90000); //Sleep for synchronization to happen.
//        new SyncAllDataAsyncTask<Enquiry>();

        //and i view that particular enquiry
        viewAllEnquiriesPage.navigateToPage();
        viewAllEnquiriesPage.clickElementWithText(enquiry.getEnquirerName());
        solo.sleep(3000);

        //then  i should see both children as potential matches
//        viewAllEnquiriesPage.isChildPresent(child1);
//        viewAllEnquiriesPage.isChildPresent(child2);

    }
}
