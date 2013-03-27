package com.rapidftr.activity;


import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import junit.framework.Assert;
import org.json.JSONException;

import com.rapidftr.RapidFtrApplication;

public class SearchChildrenActivity extends BaseActivityIntegrationTest{

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
    }

    @Override
    public void tearDown() throws  Exception{
        loginPage.logout();
//        loginPage.clickLoginButton();
        super.tearDown();
    }

    //fdf

    public void testSearchChildAndViewDetail() throws JSONException {
        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        Child child=new Child(getAlphaNumeric(5), "admin","{\"name\":\"SearchTest\"}");
        repository.createOrUpdate(child);
        searchPage.navigateToSearchTab();
        searchPage.searchChild("SearchTest");
        assertTrue(searchPage.isChildPresent(child.getUniqueId(), "SearchTest"));
        viewAllChildrenPage.clickChild(child.getUniqueId());
        viewAllChildrenPage.verifyChildDetails(child);

    }
                   //sdsf
    public void testErrorMessageOnNoSearchResultFound(){
        searchPage.navigateToSearchTab();
        searchPage.searchChild("InvalidChild");
        assertTrue(isTextPresent("No Record Found"));

    }

    public void testUserCanEditChildCreatedByOtherUserViaSearch() throws JSONException{
        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        Child child =new Child(getAlphaNumeric(5), "admin","{\"name\":\"SearchEditTest\"}");
        repository.createOrUpdate(child);
        searchPage.navigateToSearchTab();
        searchPage.searchChild(child.getName());
        viewAllChildrenPage.clickChild(child.getUniqueId());
        childPage.selectEditChild();
        childPage.selectFormSection("Basic Identity");
        childPage.enterChildName("Edited Child");
        childPage.save();
        solo.waitForText("Saved Record Successfully");
//        assertTrue(childPage.getChildName("Edited child"));
        assertTrue(isEditedTextPresent("Edited Child"));
        assertTrue(isTextPresent("Edited Child"));
        assertTrue(isEditTextPresent("Edited Child"));
    }


}
