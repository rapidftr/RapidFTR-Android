package com.rapidftr.activity;

import com.rapidftr.R;
import com.rapidftr.activity.pages.LoginPage;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;

import java.io.IOException;
import java.util.UUID;

import static com.rapidftr.utils.RapidFtrDateTime.now;
import static com.rapidftr.utils.http.FluentRequest.http;

public class DataSyncingIntegrationTest extends BaseActivityIntegrationTest {

    private static final long SYNC_TIMEOUT = 5000; // 2 mins

    ChildRepository childRepository;
    EnquiryRepository enquiryRepository;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        enquiryRepository = application.getInjector().getInstance(EnquiryRepository.class);
        childRepository = application.getInjector().getInstance(ChildRepository.class);
        deleteRecordsOnServer("children");
        deleteRecordsOnServer("enquiries");
    }

    @Override
    public void tearDown() throws Exception {
        try {
            childRepository.close();
            enquiryRepository.close();
        } catch (Exception e) {
        } finally {
            super.tearDown();
        }
    }

    public void testSyncRecordsShouldUpdateRecordAttributesAndAttachMatchingChildRecordsToEnquiries() throws Exception {
        String timeStamp = now().defaultFormat();
        String childId = UUID.randomUUID().toString();
        String childName = UUID.randomUUID().toString().substring(0, 6);
        Child childToStore = new Child(String.format("{'unique_identifier':'%s', 'timeStamp':'%s', 'nationality':'ugandan', 'one':'1', 'name':'%s' }", childId, timeStamp, childName));
        seedChildOnServer(childToStore);

        String enquiryJSON = String.format("{ \"enquirer_name\":\"Tom Cruise\", \"name\":\"%s\"," +
                "\"nationality\":\"ugandan\",\"synced\" : \"false\"}", childName);

        Enquiry enquiryToSync = new Enquiry(enquiryJSON);
        enquiryToSync.setCreatedBy(application.getCurrentUser().getUserName());
        enquiryRepository.createOrUpdate(enquiryToSync);

        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));

        solo.waitForText("Records Successfully Synchronized");
        waitUntilSeededRecordIsSynced(childId);

        Enquiry enquiry = enquiryRepository.get(enquiryToSync.getUniqueId());

        Child child = childRepository.get(childId);

        assertTrue(enquiry.getPotentialMatchingIds().contains(child.getId()));
        assertTrue(child.isSynced());
        assertTrue(enquiry.isSynced());

        searchPage.navigateToSearchTab();
        searchPage.searchChild(childName);
        searchPage.clickSearch();
        assertTrue(searchPage.isChildPresent(child.getName(), childName));

        viewAllEnquiriesPage.navigateToPage();
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(enquiry));

        viewAllEnquiriesPage.clickElementWithText(enquiry.getEnquirerName());
        solo.sleep(3000);

        viewAllEnquiriesPage.isChildPresent(childToStore);
    }


    private void seedChildOnServer(Child child) throws JSONException, IOException {
        http()
                .context(application)
                .host(LoginPage.LOGIN_URL)
                .config(HttpConnectionParams.CONNECTION_TIMEOUT, 15000)
                .path("/api/children")
                .param("child", child.values().toString())
                .post();
    }

    private void deleteRecordsOnServer(String records) throws JSONException, IOException {
        http()
                .context(application)
                .host(LoginPage.LOGIN_URL)
                .config(HttpConnectionParams.CONNECTION_TIMEOUT, 15000)
                .path(String.format("/api/%s/destroy_all", records))
                .delete();
    }

    private void waitUntilSeededRecordIsSynced(String id) throws JSONException {
        long now = System.currentTimeMillis();

        boolean childFound = false;
        while (!childFound) {
            if (System.currentTimeMillis() > now + SYNC_TIMEOUT) {
                throw new RuntimeException("Waiting for record to be synced has timed out.");
            }
            try {
                childRepository.get(id);
                childFound = true;
            } catch (NullPointerException e) {
                continue;
            }
        }
    }


}

