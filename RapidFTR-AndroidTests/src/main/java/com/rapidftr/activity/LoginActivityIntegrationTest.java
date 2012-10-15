package com.rapidftr.activity;

import android.app.ActivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodSession;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.activity.pages.Page;
import android.content.*;
import org.junit.Ignore;


public class LoginActivityIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public static final String LOGIN_URL = "dev.rapidftr.com:3000";
    public static final String PASSWORD = "rapidftr";
    public static final String USERNAME = "rapidftr";
    public Solo solo;
    private Context content;

    public LoginActivityIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        Page.setSolo(solo);
    }

    @Override
    public void tearDown() throws Exception {

        solo.finishOpenedActivities();

    }

    public void testIncorrectLoginCredentials(){
        Page.loginPage.login("wrongUsername", "wrongPassword", LOGIN_URL);
        assertTrue("Incorrect Username Or Password", solo.waitForText("Incorrect username or password"));
    }

    @Ignore
    public void testNoLoginDetailsErrorMessages(){
          Page.loginPage.login(" "," "," ");
          Page.loginPage.getNoUserNameErrorMessage().equals("Username is required");

    }

    public void testSuccessfulLogin() {
        Page.loginPage.login(USERNAME, PASSWORD, LOGIN_URL);
        assertTrue("Login should be successful", solo.waitForText("Login Successful"));
        Page.loginPage.logout();
    }

     public void testUserAbleToSeeLastSuccessfulLoginUrl() {
         Page.loginPage.login(USERNAME, PASSWORD, LOGIN_URL);
         solo.waitForText("Login Successful");
         Page.loginPage.logout();
         System.out.println("logged out ");
         Page.loginPage.clickLoginButton();
         System.out.println("logged in ");
         Page.loginPage.changeURL();
         Page.loginPage.getUrl().equals(LOGIN_URL);
    }



//    @Ignore
//    public void testUserCanChangeUrlAndLoginInAnotherAccount() {
//
//    }
}
