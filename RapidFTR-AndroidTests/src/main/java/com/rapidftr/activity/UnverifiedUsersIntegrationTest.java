package com.rapidftr.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;

public class UnverifiedUsersIntegrationTest extends BaseActivityIntegrationTest{
    boolean wifi=false;
    Child child;
    String password ="a";
    String organisation="TW";
    ChildRepository repository;
    String childName;

    @Override
    public void setUp() throws Exception{
        super.setUp();
         childName=getAlphaNumeric(5);
    }

    public void testUnverifiedUserCreationAndSyncRecordsToServer(){
//        turnWifi(wifi);
        solo.clickOnText(solo.getString(R.string.signup));
        loginPage.registerUnverifiedUser(childName, password, password, childName, organisation);
        loginPage.login(childName,password,loginPage.LOGIN_URL);
        assertTrue(solo.waitForText("Login Successful"));
    }

    public void estUnverifiedUserSyncProcessAfterMakingAsVerifiedUser(){

    }

    public void estUserAbleToSignUpInOfflineMode(){

    }

    public void estVerifiedUserAbleToLoginOfflineAfterASuccessfulOnlineLogin(){

    }






    protected void turnWifi(boolean status) {
        try {
            WifiManager wifiManager = (WifiManager) getInstrumentation()
                    .getTargetContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(status);
        } catch (Exception ignored) {
        }
    }
}
