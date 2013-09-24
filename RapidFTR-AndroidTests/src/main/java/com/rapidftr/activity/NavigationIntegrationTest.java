package com.rapidftr.activity;

import android.view.View;
import com.rapidftr.R;
import junit.framework.Assert;

public class NavigationIntegrationTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        Assert.assertTrue(solo.waitForText("Login Successful"));
        waitUntilTextDisappears("Login Successful");
    }

    public void testClickNavBars() throws Exception {
        View child_nav_bar;
        View enquiry_nav_bar;

        assertAbsenceOfView(R.id.enquiry_nav_bar);
        child_nav_bar = solo.getView(R.id.child_nav_bar);
        assertEquals(View.VISIBLE, child_nav_bar.getVisibility());

        solo.clickOnButton("Enquiry");
        solo.sleep(1000);

        assertAbsenceOfView(R.id.child_nav_bar);
        enquiry_nav_bar = solo.getView(R.id.enquiry_nav_bar);
        assertEquals(View.VISIBLE, enquiry_nav_bar.getVisibility());

        solo.clickOnButton("Child");
        solo.sleep(1000);

        assertAbsenceOfView(R.id.enquiry_nav_bar);
        child_nav_bar = solo.getView(R.id.child_nav_bar);
        assertEquals(View.VISIBLE, child_nav_bar.getVisibility());
    }

    public void testTabStyleIsSelected() throws Exception {
        View child_nav_bar;

        child_nav_bar = solo.getView(R.id.child_nav_bar);
        assertEquals(View.VISIBLE, child_nav_bar.getVisibility());


        solo.clickOnButton("Enquiry");
        solo.sleep(1000);

    }

    private void assertAbsenceOfView(int viewId) {
        for (View v : solo.getCurrentViews()){
            assertNotSame(v.getId(), viewId);
        }
    }
}
