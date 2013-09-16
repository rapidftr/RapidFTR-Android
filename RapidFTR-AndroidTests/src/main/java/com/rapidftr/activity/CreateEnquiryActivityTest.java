package com.rapidftr.activity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class CreateEnquiryActivityTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        waitUntilTextDisappears("Login Successful");
        enquiryPage.navigateToCreatePage();
    }

    public void testFormSectionsDisplayed(){
        List<String> actualSections = enquiryPage.getAllFormFields();
        List<String> expectedSections = new ArrayList<String>(asList(new String[]{"Reporter Name", "Reporter Nationality"}));
        for (Object field : expectedSections) {
            assertTrue(String.format("Visibility of %s", field), actualSections.contains(field));
        }
    }
}
