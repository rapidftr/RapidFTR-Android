package com.rapidftr.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.activity.pages.LoginPage;
import static com.rapidftr.activity.pages.LoginPage.*;

public class LoginActivityIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public Solo solo;
    public LoginPage loginPage;

    public LoginActivityIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        loginPage = new LoginPage(solo);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testIncorrectLoginCredentials(){
        loginPage.login("wrongUsername", "wrongPassword", LOGIN_URL);
        assertTrue("Incorrect Username Or Password", solo.waitForText("Incorrect username or password"));
        loginPage.login(USERNAME, PASSWORD,"http://dev.rapidftr.com:abc");
        assertTrue(solo.waitForText("Unable to connect to the server, please contact your system administrator"));
    }


    public void testNoLoginDetailsErrorMessages(){
          loginPage.login("","","");
          loginPage.getUserNameRequiredMessage().equals("Username is required");
          loginPage.getPasswordRequiredMEssage().equals("Password is required");
          loginPage.getURLRequiredMessage().equals("Server URL is required");

    }

    public void testSuccessfulLogin() {
        loginPage.login(USERNAME, PASSWORD, LOGIN_URL);
        assertTrue("Login should be successful", solo.waitForText("Login Successful"));
        loginPage.logout();
    }

     public void testUserAbleToSeeLastSuccessfulLoginUrl() {
         loginPage.login(USERNAME, PASSWORD, LOGIN_URL);
         solo.waitForText("Login Successful");
         loginPage.logout();
         loginPage.clickLoginButton();
         loginPage.changeURL();
         loginPage.getUrl().equals(LOGIN_URL);
    }

//    @Ignore
//    public void testUserCanChangeUrlAndLoginInAnotherAccount() {
//
//    }

}
