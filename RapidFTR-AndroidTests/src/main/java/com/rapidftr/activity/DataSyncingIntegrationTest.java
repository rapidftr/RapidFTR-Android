package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;

import java.io.IOException;

import static com.rapidftr.utils.RapidFtrDateTime.now;
import static com.rapidftr.utils.http.FluentRequest.http;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DataSyncingIntegrationTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
    }

    public void workInProgressRecordIsSuccessfullySyncedAndIdPopulatedWithCouchIdFromServer() throws JSONException, IOException {
        String timeStamp = now().defaultFormat();
        seed(new Child(String.format("{ '_id' : '123', 'timeStamp' : '%s', 'test2' : 'value2' }", timeStamp)));
        solo.clickOnMenuItem("Synchronize");
        solo.waitForDialogToClose(10000);
        ChildRepository repository = RapidFtrApplication.getInstance().getInjector().getInstance(ChildRepository.class);

        Child retrievedChild = repository.get("123");
        assertThat(retrievedChild.get("timeStamp").toString(), is(timeStamp));
    }

    private void seed(Child child) throws JSONException, IOException {
        http()
        .path(String.format("/children/%s", child.getId()))
        .context(RapidFtrApplication.getInstance())
        .param("child", child.toString())
        .put();
    }

}
