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

    private static final long SYNC_TIMEOUT = 90000; // 1.5 min

    ChildRepository childRepository;
    EnquiryRepository enquiryRepository;
    String userName;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        enquiryRepository = application.getInjector().getInstance(EnquiryRepository.class);
        childRepository = application.getInjector().getInstance(ChildRepository.class);
        userName = application.getCurrentUser().getUserName();
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

    public void testSyncShouldUpdateRecordsAndAttachMatchingChildRecordsToEnquiry() throws Exception {
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

        synchronizeAllRecords();

        Enquiry enquiry = enquiryRepository.get(enquiryToSync.getUniqueId());

        Child child = childRepository.get(childId);

        assertTrue(enquiry.getPotentialMatchingIds().contains(child.getInternalId()));
        recordsShouldBeSynced(child, enquiry);

        searchPage.navigateToSearchTab();
        searchPage.searchChild(childName);
        searchPage.clickSearch();
        assertTrue(searchPage.isChildPresent(child.getName(), childName));
    }

    public void testShouldGetRecordsUpdatedOnTheServerAfterSync() throws Exception {
        String timeStamp = now().defaultFormat();
        String childName = UUID.randomUUID().toString().substring(0, 6);
        String childJSON = String.format("{'created_by' : '%s', 'timeStamp' : '%s', 'nationality' : 'uganda', 'name' : '%s' }", userName, timeStamp, childName);

        Child child = new Child(childJSON);
        String enquiryJSON = String.format("{'created_by' : '%s', 'enquirer_name' : 'Wire', 'name' : 'Alex', 'synced' : 'false'}", userName);
        Enquiry enquiry = new Enquiry(enquiryJSON);

        childRepository.createOrUpdate(child);
        enquiryRepository.createOrUpdate(enquiry);

        recordsShouldNotHaveInternalIds(child, enquiry);

        synchronizeAllRecords();

        String childUniqueId = child.getUniqueId();
        Child childAfterSync = childRepository.get(childUniqueId);
        Enquiry enquiryAfterSync = enquiryRepository.get(enquiry.getUniqueId());

        recordsShouldHaveInternalIds(childAfterSync, enquiryAfterSync);
        recordsShouldBeSynced(childAfterSync, enquiryAfterSync);

        String updatedChildJSON = String.format("{'_id' : '%s' ,'created_by' : '%s', 'nationality' : 'uganda', 'name' : 'Albert', 'gender' : 'male' }", childAfterSync.getInternalId(), userName);
        updateRecordOnServer(new Child(updatedChildJSON));

        String enquiryUniqueId = enquiry.getUniqueId();
        String updatedEnquiryJSON = String.format("{'created_by' : '%s', 'enquirer_name' : 'James Wire', 'nationality' : 'uganda', 'unique_identifier' : '%s'}", userName, enquiryUniqueId);
        enquiryRepository.createOrUpdate(new Enquiry(updatedEnquiryJSON));
        Enquiry enquiryAfterUpdate = enquiryRepository.get(enquiryUniqueId);

        assertEquals("James Wire", enquiryAfterUpdate.getEnquirerName());
        assertEquals("", enquiryAfterUpdate.getPotentialMatchingIds());

        synchronizeAllRecords();

        viewEnquiryPage.navigateToPage(enquiryAfterUpdate.getEnquirerName());
        viewEnquiryPage.isChildPresent(childUniqueId);
    }

    private void recordsShouldBeSynced(Child childAfterSync, Enquiry enquiryAfterSync) {
        assertTrue(childAfterSync.isSynced());
        assertTrue(enquiryAfterSync.isSynced());
    }

    private void recordsShouldHaveInternalIds(Child childAfterSync, Enquiry enquiryAfterSync) throws JSONException {
        assertNotNull(childAfterSync.getInternalId());
        assertNotNull(enquiryAfterSync.getInternalId());
    }

    private void synchronizeAllRecords() throws JSONException {
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.waitForText("Records Successfully Synchronized");
        waitUntilRecordsAreSynced();
    }

    private void recordsShouldNotHaveInternalIds(Child child, Enquiry enquiry) throws JSONException {
        assertNull(child.getInternalId());
        assertNull(enquiry.getInternalId());
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

    private void updateRecordOnServer(Child child) throws JSONException, IOException {
        http()
                .context(application)
                .host(LoginPage.LOGIN_URL)
                .config(HttpConnectionParams.CONNECTION_TIMEOUT, 15000)
                .path(String.format("/api/children/%s", child.getInternalId()))
                .param("child", child.values().toString())
                .put();
    }

    private void deleteRecordsOnServer(String records) throws JSONException, IOException {
        http()
                .context(application)
                .host(LoginPage.LOGIN_URL)
                .config(HttpConnectionParams.CONNECTION_TIMEOUT, 15000)
                .path(String.format("/api/%s/destroy_all", records))
                .delete();
    }

    private void waitUntilRecordsAreSynced() throws JSONException {
        long endSyncTime = System.currentTimeMillis() + SYNC_TIMEOUT;

        while (System.currentTimeMillis() < endSyncTime) {
            continue;
        }
    }


}

