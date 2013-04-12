package com.rapidftr.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.pages.LoginPage;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.test.utils.RapidFTRDatabase;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class UnverifiedUsersIntegrationTest extends BaseActivityIntegrationTest{
    boolean wifi=false;
    String password ="a";
    String organisation="TW";
    ChildRepository repository;
    String childName;
    List childDetails;
    Child child;

    @Override
    public void setUp() throws Exception{
        super.setUp();
        childName=getAlphaNumeric(5);
        unverifiedUserPage.clickSignUpLink();
        unverifiedUserPage.registerUnverifiedUser(childName, password, password,childName, organisation);
        solo.sleep(5);
        loginPage.login(childName, password, loginPage.LOGIN_URL);
        solo.waitForText("Login Successful");
        repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        RapidFTRDatabase.deleteChildren();

    }

    @Override
    public void tearDown() throws Exception{
        repository.close();
        super.tearDown();
    }

    public void testUnverifiedUserCreationErrorMessages(){
        loginPage.logout();
        unverifiedUserPage.clickSignUpLink();
        unverifiedUserPage.registerUnverifiedUser(" "," "," "," "," ");
        //try hash map here
        List<String> expectedErrorMessage = new ArrayList<String>(asList(new String[]{"Username is required","Password is required","Re-type password",
                "Full name required","Organisation is required"}));
        List<String> actualErrorMessage=unverifiedUserPage.getFieldsErrorMessages();
        assertTrue(expectedErrorMessage.equals(actualErrorMessage));
    }

    public void testUnverifiedUserAbleToSyncRecordsToServer() throws JSONException,InterruptedException {
        childPage.navigateToRegisterPage();
        childPage.selectFormSection("Automation Form");
        childPage.registerChild();
        viewAllChildrenPage.navigateToViewAllTab();
        childDetails = repository.getChildrenByOwner();
        child=new Child(childDetails.get(0).toString());
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        assertTrue(unverifiedUserPage.verifySyncLocationPopUp());
        unverifiedUserPage.enterSyncLocation(LoginPage.LOGIN_URL);
        assertTrue(repository.exists(child.getUniqueId()));
        List<Child> children=repository.getMatchingChildren(child.getUniqueId());
        assertEquals(1,children.size());
    }

    public void testUnverifiedUserUnableToViewVerifiedUserRecords() throws JSONException,InterruptedException{
        child= new Child(getAlphaNumeric(5), "admin", "{'name' : 'adminuser'}");
        repository.createOrUpdate(child);
        viewAllChildrenPage.navigateToViewAllTab();
        solo.clickOnMenuItem(solo.getString(R.string.synchronize_all));
        unverifiedUserPage.enterSyncLocation(LoginPage.LOGIN_URL);
        assertFalse(searchPage.isChildPresent(child.getUniqueId(),child.getName()));

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
