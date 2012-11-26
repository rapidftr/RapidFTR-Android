package com.rapidftr.activity;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;

public class ViewAllChildrenActivityIntegrationTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
    }

    public void testDisplayAllChildren() throws JSONException {
        ChildRepository repository = RapidFtrApplication.getInstance().getInjector().getInstance(ChildRepository.class);
        repository.createOrUpdate(new Child("id1", "rapidftr","{\"name\":\"Test1\"}"));
        repository.createOrUpdate(new Child("id2", "rapidftr", "{\"name\":\"Test2\"}"));
        viewAllChildrenPage.navigateToViewAllPage();
        assertTrue(viewAllChildrenPage.isChildPresent("id1", "Test1"));
        assertTrue(viewAllChildrenPage.isChildPresent("id2", "Test2"));
    }

    public void testClickOnChildShouldShowViewPage() throws JSONException {
        ChildRepository repository = RapidFtrApplication.getInstance().getInjector().getInstance(ChildRepository.class);
        Child child1 = new Child("id1", "rapidftr", "{\"name\":\"Test1\"}");
        repository.createOrUpdate(child1);
        Child child2 = new Child("id2", "rapidftr", "{\"name\":\"Test2\"}");
        repository.createOrUpdate(child2);

        viewAllChildrenPage.navigateToViewAllPage();
        viewAllChildrenPage.clickChild("id1");
        viewAllChildrenPage.verifyChildDetails(child1);
    }
}
