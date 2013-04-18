package com.rapidftr.activity;

import android.test.suitebuilder.annotation.Smoke;
import com.rapidftr.R;

import static com.rapidftr.activity.pages.LoginPage.LOGIN_URL;

public class LoginActivityIntegrationTest extends BaseActivityIntegrationTest {

    public void testIncorrectLoginCredentials(){
        loginPage.login("wrongUsername", "wrongPassword", LOGIN_URL);
        assertTrue(solo.waitForText("Incorrect username or password"));
    }

    public void testNoLoginDetailsErrorMessages(){
          loginPage.login("", "", "");
          assertTrue(loginPage.getUserNameRequiredMessage().equals("Username is required"));
          assertTrue(loginPage.getPasswordRequiredMessage().equals("Password is required"));
    }

     public void testUserAbleToSeeLastSuccessfulLoginUrl() throws Exception {
         loginPage.login();
         waitUntilTextDisappears("Login Successful");
         loginPage.logout();
         loginPage.changeURL();
         assertTrue(loginPage.getUrl().equals(LOGIN_URL));
    }

    @Smoke
    public void testCannotNavigateBackToLoginPageOnceLoggedIn(){
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.goBack();
	    assertTrue(solo.getCurrentActivity().isFinishing());
    }

    public void testUserIsAlertedWhenAttemptingToLogoutWhileSyncInProgress() throws Exception {
        loginPage.login();
        waitUntilTextDisappears("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(1000);
        solo.clickOnMenuItem(solo.getString(R.string.log_out));

        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.confirm_logout_message)));

        solo.clickOnButton(solo.getString(R.string.log_out));
        solo.assertCurrentActivity("should log out and go to login page", LoginActivity.class);
    }


    public void testPasswordReset() throws Exception {
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.change_password));
        changePasswordPage.changePassword("admin", "rapidftr", "rapidftr");
        assertTrue(solo.waitForText("Password Changed Successfully"));
        waitUntilTextDisappears("Password Changed Successfully");
        loginPage.logout();
        loginPage.login();
        assertTrue(solo.waitForText("Incorrect username or password"));
        loginPage.login("admin","rapidftr",LOGIN_URL);
        solo.waitForText("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.change_password));
        changePasswordPage.changePassword("rapidftr","admin","admin");
        waitUntilTextDisappears("Password Changed Successfully");
    }

    public void testPasswordResetErrors(){
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.change_password));
        changePasswordPage.changePassword("","","");
        assertTrue(changePasswordPage.getCurrentPasswordRequiredMessage().equals("All fields are mandatory"));
        assertTrue(changePasswordPage.getNewPasswordRequiredMessage().equals("All fields are mandatory"));
        assertTrue(changePasswordPage.getNewPasswordConfirmRequiredMessage().equals("All fields are mandatory"));
    }

    public void testPasswordResetWrongCurrentPassword(){
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.change_password));
        changePasswordPage.changePassword("rapidfr","rapidftr","rapidftr");
        assertTrue(solo.waitForText("Could not change password. Try again"));
        changePasswordPage.changePassword("rapidftr","rapidftr","rapitr");
        assertTrue(changePasswordPage.getNewPasswordConfirmRequiredMessage().equals("Password mismatch"));
    }



}
