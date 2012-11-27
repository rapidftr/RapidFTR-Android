package com.rapidftr.activity;

import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.test.utils.RapidFTRDatabase;
import org.json.JSONException;

import java.io.IOException;

import static com.rapidftr.utils.RapidFtrDateTime.now;
import static com.rapidftr.utils.http.FluentRequest.http;

public class DataSyncingIntegrationTest extends BaseActivityIntegrationTest {
    ChildRepository repository;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        String timeStamp = now().defaultFormat();
        seed(new Child(String.format("{ '_id' : '123456', 'timeStamp' : '%s', 'test2' : 'value2', 'unique_identifier' : 'abcd1234', 'one' : '1' }", timeStamp)));
        repository = RapidFtrApplication.getInstance().getInjector().getInstance(ChildRepository.class);
        RapidFTRDatabase.deleteChildren();
    }

    public void testRecordIsSuccessfullyDownloadedFromServer() throws JSONException, IOException, InterruptedException {
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        Thread.sleep(5000); //Sleep for synchronization to happen.


        assertTrue(repository.exists("abcd1234"));
        Child child = repository.get("abcd1234");
        assertEquals("123456", child.optString("_id"));
    }

    public void testRecordShouldBeUploadedToServer() throws JSONException, InterruptedException {

        Child childToBeSynced = new Child("'unique_identifier' : 'xyz4321','name' : 'moses'");
        repository.createOrUpdate(childToBeSynced);
        assertFalse(childToBeSynced.isSynced());
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        Thread.sleep(5000); //Sleep for synchronization to happen.
        assertTrue(repository.exists("xyz4321"));
        assertTrue(childToBeSynced.isSynced());

    }

    private void seed(Child child) throws JSONException, IOException {
        http()
        .path(String.format("/children/%s", child.getId()))
        .context(RapidFtrApplication.getInstance())
        .param("child", child.toString())
        .put();
    }

}
