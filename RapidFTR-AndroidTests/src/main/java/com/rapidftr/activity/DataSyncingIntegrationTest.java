package com.rapidftr.activity;

import android.view.KeyEvent;
import com.rapidftr.R;
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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        repository = application.getInjector().getInstance(ChildRepository.class);
        RapidFTRDatabase.deleteChildren();
    }

    @Override
    public void tearDown() throws Exception {
        repository.close();
        super.tearDown();
    }

    public void testRecordIsSuccessfullyDownloadedFromServer() throws JSONException, IOException, InterruptedException {
        String timeStamp = now().defaultFormat();
        seedDataOnServer(new Child(String.format("{ '_id' : '123456', 'timeStamp' : '%s', 'test2' : 'value2', 'unique_identifier' : 'abcd1234', 'one' : '1', 'name' : 'jen' }", timeStamp)));
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        Thread.sleep(20000); //Sleep for synchronization to happen.
//         waitUntilSyncCompletion();
        Child child = repository.get("abcd1234");
        assertEquals("123456", child.optString("_id"));
        searchPage.navigateToSearchTab();
        searchPage.searchChild(child.optString("unique_identifier"));
        assertTrue(searchPage.isChildPresent(child.optString("unique_identifier"), "jen"));
        loginPage.logout();
    }

    public void testRecordShouldBeUploadedToServer() throws JSONException, InterruptedException {

        Child childToBeSynced = new Child(getAlphaNumeric(6), "admin", "{'name' : 'moses'}");
        repository.createOrUpdate(childToBeSynced);
        assertFalse(childToBeSynced.isSynced());
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
//        solo.getCurrentActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        waitUntilSyncCompletion();
        Thread.sleep(20000); //Sleep for synchronization to happen.
        assertTrue(repository.exists(childToBeSynced.getUniqueId()));
        List<Child> children = repository.getMatchingChildren(childToBeSynced.getUniqueId());
        assertEquals(1, children.size());
        assertTrue(children.get(0).isSynced());
        loginPage.logout();
    }



    public void testSynchronizationShouldCancelIfTheUserIsLoggingOutFromTheApplication() throws JSONException, InterruptedException {
        Child child1 = new Child("abc4321", "admin", "{'name' : 'moses'}");
        Child child2 = new Child("qwe4321", "admin", "{'name' : 'james'}");
        Child child3 = new Child("zxy4321", "admin", "{'name' : 'kenyata'}");
        Child child4 = new Child("uye4321", "admin", "{'name' : 'keburingi'}");
        seedDataToRepository(child1, child2, child3, child4);
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        Thread.sleep(1000);
        solo.clickOnMenuItem(solo.getString(R.string.log_out));
        //Robotium doesn't support asserting on notification bar by default. Below is the hack to get around it.
        solo.clickOnButton(solo.getString(R.string.log_out)); //As the synchronization is still happening we'll get an dialog box for the user action.
        solo.waitForText(solo.getString(R.string.logout_successful));
    }


    public void seedDataToRepository(Child... children) throws JSONException {
        for (Child child : children) {
            repository = application.getInjector().getInstance(ChildRepository.class);
            repository.createOrUpdate(child);
        }
    }

    public void seedDataOnServer(Child child) throws JSONException, IOException {
        http()
                .context(application)
                .host(LoginPage.LOGIN_URL)
                .config(HttpConnectionParams.CONNECTION_TIMEOUT, 15000)
                .path("/api/children")
                .param("child", child.values().toString())
                .post();
    }

    public void waitUntilSyncCompletion() {

        for(int i=0;i<10;i++){
            solo.sendKey(KeyEvent.KEYCODE_MENU);
            if(solo.searchText("Synchronize All",false)){
                solo.sleep(10);
            }else{
                break;
            }
        }
    }


}
