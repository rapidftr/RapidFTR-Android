package com.rapidftr.activity;

import static com.rapidftr.activity.pages.LoginPage.*;

public class LoginActivityIntegrationTest extends BaseActivityIntegrationTest<LoginActivity> {

    public LoginActivityIntegrationTest() {
        super(LoginActivity.class);
    }

    public void testIncorrectLoginCredentials(){
        loginPage.login("wrongUsername", "wrongPassword", LOGIN_URL);
        assertTrue("Expected to see Incorrect Username Or Password message", solo.waitForText("Incorrect username or password"));
        loginPage.login(USERNAME, PASSWORD,"http://dev.rapidftr.com:abc");
        assertTrue(solo.waitForText("Unable to connect to the server, please contact your system administrator"));
    }

    public void testNoLoginDetailsErrorMessages(){
          loginPage.login("","","");
          assertTrue(loginPage.getUserNameRequiredMessage().equals("Username is required"));
          assertTrue(loginPage.getPasswordRequiredMEssage().equals("Password is required"));
          assertTrue(loginPage.getURLRequiredMessage().equals("Server URL is required"));
    }

     public void testUserAbleToSeeLastSuccessfulLoginUrl() {
         loginPage.login(USERNAME, PASSWORD, LOGIN_URL);
         solo.waitForText("Login Successful");
         loginPage.logout();
         loginPage.clickLoginButton();
         loginPage.changeURL();
         assertTrue(loginPage.getUrl().equals(LOGIN_URL));
    }


    public void testSuccessfulLogin() {
        loginPage.login();
        assertTrue("Login should be successful", solo.waitForText("Login Successful"));
        loginPage.logout();
    }

}
