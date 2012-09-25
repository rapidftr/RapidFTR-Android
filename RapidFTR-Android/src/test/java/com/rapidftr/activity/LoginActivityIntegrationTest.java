package com.rapidftr.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;

public class LoginActivityIntegrationTest  extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;
    public LoginActivityIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testSuccessfulLogin(){
        solo.enterText(0,"rapidftr");
        solo.enterText(1,"rapidftr");
        solo.enterText(2,"dev.rapidftr.com:3000");
        solo.clickOnButton("Log In");
        assertTrue("Login should be successful", solo.searchText("Login Successful"));
    }
}
