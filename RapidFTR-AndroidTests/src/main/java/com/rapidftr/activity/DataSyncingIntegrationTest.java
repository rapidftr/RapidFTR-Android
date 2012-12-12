package com.rapidftr.activity;

import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.pages.LoginPage;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.test.utils.RapidFTRDatabase;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static com.rapidftr.utils.RapidFtrDateTime.now;
import static com.rapidftr.utils.http.FluentRequest.http;

public class DataSyncingIntegrationTest extends BaseActivityIntegrationTest {
    ChildRepository repository;
    RapidFtrApplication context;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        context = RapidFtrApplication.getApplicationInstance();
        repository = context.getInjector().getInstance(ChildRepository.class);
        RapidFTRDatabase.deleteChildren();
    }

    public void estRecordIsSuccessfullyDownloadedFromServer() throws JSONException, IOException, InterruptedException {
        String timeStamp = now().defaultFormat();
        seed(new Child(String.format("{ '_id' : '123456', 'timeStamp' : '%s', 'test2' : 'value2', 'unique_identifier' : 'abcd1234', 'one' : '1', 'name' : 'jen' }", timeStamp)));
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        Thread.sleep(10000); //Sleep for synchronization to happen.

        assertTrue(repository.exists("abcd1234"));
        Child child = repository.get("abcd1234");
        assertEquals("123456", child.optString("_id"));
        searchPage.navigateToSearchPage();
        searchPage.searchChild(child.optString("_id"));
        assertTrue(searchPage.isChildPresent(child.optString("_id"), "jen"));
    }

    public void testRecordShouldBeUploadedToServer() throws JSONException, InterruptedException {

        Child childToBeSynced = new Child("xyz4321", "rapidftr", "{'name' : 'moses'}");
        repository.createOrUpdate(childToBeSynced);
        assertFalse(childToBeSynced.isSynced());
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.waitForText("Sync Successful");
//        Thread.sleep(10000); //Sleep for synchronization to happen.
        assertTrue(repository.exists("xyz4321"));
        List<Child> children = repository.getMatchingChildren("xyz4321");
        assertEquals(1, children.size());
        assertTrue(children.get(0).isSynced());
    }



    private void seed(Child child) throws JSONException, IOException {
        http()
        .context(context)
        .host(LoginPage.LOGIN_URL)
        .config(HttpConnectionParams.CONNECTION_TIMEOUT, 15000)
        .path(String.format("/children", child.getId()))
        .param("child", child.toString())
        .post();
    }

}
