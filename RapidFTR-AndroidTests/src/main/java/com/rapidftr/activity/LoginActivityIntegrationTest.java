package com.rapidftr.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.suitebuilder.annotation.Smoke;
import com.rapidftr.R;

import static com.rapidftr.activity.pages.LoginPage.*;

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
	    assertTrue(solo.getCurrentActivity().isFinishing());
    }

    public void testUserIsAlertedWhenAttemptingToLogoutWhileSyncInProgress() throws InterruptedException {
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        solo.sleep(1000);
        solo.clickOnMenuItem(solo.getString(R.string.log_out));

        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.confirm_logout_message)));

        solo.clickOnButton(solo.getString(R.string.log_out));
        solo.assertCurrentActivity("should log out and go to login page", LoginActivity.class);
    }

    public void testUserAbleToLoginOfflineAfterOneSuccessfulLogin(){
        loginPage.login();
        solo.waitForText("Login Successful");
        boolean wifi=false;
        turnWifi(wifi);
        System.out.println("hello");
    }

    public void testPasswordReset(){
        loginPage.login();
        solo.waitForText("Login Successful");
        solo.clickOnMenuItem(solo.getString(R.string.change_password));
        changePasswordPage.changePassword("rapidftr","rapidftr","rapidftr");
        assertTrue(solo.waitForText("Password Changed Successfully"));
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



    protected void turnWifi(boolean status) {
        try {
            WifiManager wifiManager = (WifiManager) getInstrumentation()
                    .getTargetContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(status);
        } catch (Exception ignored) {
//            don't interrupt test execution, if there
            // is no permission for that action
        }
    }


}
