package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import junit.framework.Assert;
import org.json.JSONException;

public class ViewAllChildrenActivityIntegrationTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
//        Thread.sleep(10000);
    }

    @Override
    public void tearDown() throws Exception{
        loginPage.logout();
        super.tearDown();
    }

    public void testDisplayAllChildren() throws JSONException {
        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        repository.createOrUpdate(new Child("id1", "rapidftr","{\"name\":\"Test1\"}"));
        repository.createOrUpdate(new Child("id2", "rapidftr", "{\"name\":\"Test2\"}"));
        viewAllChildrenPage.navigateToViewAllFromHome();
        assertTrue(viewAllChildrenPage.isChildPresent("id1", "Test1"));
        assertTrue(viewAllChildrenPage.isChildPresent("id2", "Test2"));
    }

    public void testClickOnChildShouldShowViewPage() throws JSONException {

        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        Child child1 = new Child(getAlphaNumeric(4), "rapidftr", "{\"name\":\"Test1\"}");
        repository.createOrUpdate(child1);
        Child child2 = new Child(getAlphaNumeric(6), "rapidftr", "{\"name\":\"Test2\"}");
        repository.createOrUpdate(child2);
        viewAllChildrenPage.navigateToViewAllFromHome();
        viewAllChildrenPage.clickChild(child1.getUniqueId());
        viewAllChildrenPage.verifyChildDetails(child1);
    }

    public void testShowSortByName() throws JSONException {
        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        Child child1 = new Child(getAlphaNumeric(4), "rapidftr", "{\"name\":\"Test1\"}");
        repository.createOrUpdate(child1);
        Child child2 = new Child(getAlphaNumeric(6), "rapidftr", "{\"name\":\"Test2\"}");
        repository.createOrUpdate(child2);
        viewAllChildrenPage.navigateToViewAllFromHome();
        viewAllChildrenPage.testSortByName();
    }

    public void testShowSortByRecentUpdate() throws JSONException {
        ChildRepository repository = RapidFtrApplication.getApplicationInstance().getInjector().getInstance(ChildRepository.class);
        Child child1 = new Child(getAlphaNumeric(4), "rapidftr", "{\"name\":\"Test1\"}");
        repository.createOrUpdate(child1);
        Child child2 = new Child(getAlphaNumeric(6), "rapidftr", "{\"name\":\"Test2\"}");
        repository.createOrUpdate(child2);
        viewAllChildrenPage.navigateToViewAllFromHome();
        viewAllChildrenPage.testSortByRecentUpdate();
    }
}
