package com.rapidftr.activity;

import android.view.KeyEvent;
import com.rapidftr.R;
import com.rapidftr.activity.pages.LoginPage;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.rapidftr.utils.RapidFtrDateTime.now;
import static com.rapidftr.utils.http.FluentRequest.http;

public class DataSyncingIntegrationTest extends BaseActivityIntegrationTest {

    ChildRepository childRepository;
    EnquiryRepository enquiryRepository;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        enquiryRepository = application.getInjector().getInstance(EnquiryRepository.class);
        childRepository = application.getInjector().getInstance(ChildRepository.class);
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

    public void testShouldSyncRecordWithServerAndUpdateRecordAttributes() throws Exception {
        String timeStamp = now().defaultFormat();
        String childId = UUID.randomUUID().toString();
        String childName = UUID.randomUUID().toString().substring(0, 6);
        Child childToStore = new Child(String.format("{ 'unique_identifier' : '%s', 'timeStamp' : '%s', 'test2' : 'value2', 'one' : '1', 'name' : '%s' }", childId, timeStamp, childName));
        seedChildOnServer(childToStore);

        String enquiryJSON = String.format("{ \"enquirer_name\":\"Tom Cruise\", \"name\":\"%s\"," +
                "\"nationality\":\"ugandan\",\"synced\" : \"false\"}", childName);

        Enquiry enquiryToSync = new Enquiry(enquiryJSON);
        enquiryToSync.setCreatedBy(application.getCurrentUser().getUserName());
        enquiryRepository.createOrUpdate(enquiryToSync);
        solo.sleep(10000);
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(10000);
        waitUntilSyncCompletion();

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

    public void waitUntilSyncCompletion() {

        for (int i = 0; i < 20; i++) {
            solo.sendKey(KeyEvent.KEYCODE_MENU);
            if (solo.searchText("Synchronize All", false)) {
                solo.sleep(10);
            } else {
                break;
            }
        }
    }


}
