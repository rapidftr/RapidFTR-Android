package com.rapidftr.activity;

import android.view.KeyEvent;
import com.rapidftr.R;
import com.rapidftr.activity.pages.LoginPage;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.test.utils.RapidFTRDatabase;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;

import java.io.IOException;
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
        } catch (Exception e) {
        } finally {
            super.tearDown();
        }
    }

    public void xtestShouldSyncRecordWithServerAndUpdateRecordAttributes() throws JSONException, IOException, InterruptedException {
        String timeStamp = now().defaultFormat();
        String childId = UUID.randomUUID().toString();
        String childName = UUID.randomUUID().toString().substring(0, 6);
        Child childToStore = new Child(String.format("{ '_id' : '%s', 'timeStamp' : '%s', 'test2' : 'value2', 'one' : '1', 'name' : '%s' }", childId, timeStamp, childName));
        seedChildOnServer(childToStore);

        String enquiryJSON = String.format("{ \"enquirer_name\":\"Tom Cruise\", \"name\":\"%s\"," +
                "\"nationality\":\"ugandan\",\"synced\" : \"false\"}", childName);

        Enquiry enquiryToSync = new Enquiry(enquiryJSON);
        enquiryToSync.setCreatedBy(application.getCurrentUser().getUserName());
        enquiryRepository.createOrUpdate(enquiryToSync);
        viewAllEnquiriesPage.navigateToPage();
        solo.sleep(10000);
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(50000);
        waitUntilSyncCompletion();

        Child child = childRepository.get(childId);
        Enquiry enquiry = enquiryRepository.get(enquiryToSync.getUniqueId());
        assertNotNull(child);
        assertEquals(childId, new JSONArray(enquiry.matchingChildIds()).get(0));
        assertTrue(child.isSynced());
        assertTrue(enquiry.isSynced());

        searchPage.navigateToSearchTab();
        searchPage.searchChild(childName);
        searchPage.clickSearch();
        assertTrue(searchPage.isChildPresent(child.getShortId(), "derek"));

        viewAllEnquiriesPage.navigateToPage();
        assertTrue(viewAllEnquiriesPage.isEnquiryPresent(enquiry));
    }

    // TODO: REMOVE ONCE FIRST TEST PASSES
    public void xtestRecordShouldBeUploadedToServer() throws JSONException, InterruptedException {
        Child childToBeSynced = new Child(getAlphaNumeric(6), "admin", "{'name' : 'moses'}");
        childRepository.createOrUpdate(childToBeSynced);
        assertFalse(childToBeSynced.isSynced());
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(30000); //Sleep for synchronization to happen.
        assertTrue(childRepository.exists(childToBeSynced.getUniqueId()));
        List<Child> children = childRepository.getMatchingChildren(childToBeSynced.getUniqueId());
        assertEquals(1, children.size());
        assertTrue(children.get(0).isSynced());
    }

    // TODO: REMOVE ONCE FIRST TEST PASSES
    public void xtestSynchronizationShouldCancelIfTheUserIsLoggingOutFromTheApplication() throws JSONException, InterruptedException {
        Child child1 = new Child("abc4321", "admin", "{'name' : 'moses'}");
        Child child2 = new Child("qwe4321", "admin", "{'name' : 'james'}");
        Child child3 = new Child("zxy4321", "admin", "{'name' : 'kenyata'}");
        Child child4 = new Child("uye4321", "admin", "{'name' : 'keburingi'}");
        seedDataToRepository(child1, child2, child3, child4);
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(1000);
        solo.clickOnMenuItem(solo.getString(R.string.log_out));
        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.confirm_logout_message)));
        //Robotium doesn't support asserting on notification bar by default. Below is the hack to get around it.
        solo.clickOnButton(solo.getString(R.string.log_out)); //As the synchronization is still happening we'll get an dialog box for the user action.
        solo.waitForText(solo.getString(R.string.logout_successful));
    }


    public void seedDataToRepository(Child... children) throws JSONException {
        for (Child child : children) {
            childRepository = application.getInjector().getInstance(ChildRepository.class);
            childRepository.createOrUpdate(child);
        }
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

        for (int i = 0; i < 10; i++) {
            solo.sendKey(KeyEvent.KEYCODE_MENU);
            if (solo.searchText("Synchronize All", false)) {
                solo.sleep(10);
            } else {
                break;
            }
        }
    }


}
