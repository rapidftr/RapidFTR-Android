package com.rapidftr.functional;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import com.rapidftr.R;
import com.rapidftr.activity.BaseActivityIntegrationTest;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.service.ApiException;
import com.rapidftr.service.JsonClient;
import com.rapidftr.test.utils.RapidFTRDatabase;
import com.rapidftr.utils.ApplicationInjector;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnquirySyncingIntegrationTest extends BaseActivityIntegrationTest {

    private EnquiryRepository enquiryRepository;
    private JsonClient jsonClient;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.jsonClient = mock(JsonClient.class);
        loginPage.login();
        solo.waitForText("Login Successful");
        enquiryRepository = application.getInjector().getInstance(EnquiryRepository.class);
        RapidFTRDatabase.deleteEnquiries();
        Modules.override(new ApplicationInjector(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(JsonClient.class).toInstance(jsonClient);
            }
        });
    }

    public void testDoASync() throws ApiException {
        when(jsonClient.get("/api/enquiries")).thenReturn("al;sdkfj");

        clickOnSyncAllEnquiries();

    }

    private void clickOnSyncAllEnquiries() {
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
    }

}
