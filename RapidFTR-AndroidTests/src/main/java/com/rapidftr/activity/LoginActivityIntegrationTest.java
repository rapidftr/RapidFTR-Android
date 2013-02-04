package com.rapidftr.activity;

import android.test.suitebuilder.annotation.Smoke;
import com.rapidftr.activity.pages.LoginPage;

import static com.rapidftr.activity.pages.LoginPage.*;

public class LoginActivityIntegrationTest extends BaseActivityIntegrationTest {

    public void testIncorrectLoginCredentials(){
        loginPage.login("wrongUsername", "wrongPassword", LOGIN_URL);
        assertTrue(solo.waitForText("Incorrect username or password"));
    }

	public void testIncorrectServer() {
		loginPage.login(USERNAME, PASSWORD, LoginPage.LOGIN_URL+":abc");
		assertTrue(solo.waitForText("Incorrect username or password"));
	}

    public void testNoLoginDetailsErrorMessages(){
          loginPage.login("","","");
          assertTrue(loginPage.getUserNameRequiredMessage().equals("Username is required"));
          assertTrue(loginPage.getPasswordRequiredMEssage().equals("Password is required"));
    }

     public void testUserAbleToSeeLastSuccessfulLoginUrl() {
         loginPage.login(USERNAME, PASSWORD, LOGIN_URL);
         solo.waitForText("Login Successful");
         loginPage.logout();
         loginPage.changeURL();
         assertTrue(loginPage.getUrl().equals(LOGIN_URL));
    }

    @Smoke
    public void testCannotNavigateBackToLoginPageOnceLoggedIn(){
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.goBack();
        solo.assertCurrentActivity("should still be on the home page", MainActivity.class);
        loginPage.logout();
    }
}
