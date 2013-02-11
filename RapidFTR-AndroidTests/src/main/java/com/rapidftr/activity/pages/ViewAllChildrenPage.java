package com.rapidftr.activity.pages;

import android.widget.Button;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import org.json.JSONException;

import static junit.framework.Assert.assertTrue;

public class ViewAllChildrenPage {
    public Solo solo;
    int formPosition ;

    public ViewAllChildrenPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToViewAllFromHome() {
        solo.clickOnText("View All Children");
    }

    public void navigateToViewAllTab(){
        solo.clickOnText("View All");
    }
    public boolean isChildPresent(String uniqueId, String name){
        return solo.searchText(uniqueId) && solo.searchText(name);
    }

    public void clickChild(String uniqueId) {
        solo.clickOnText(uniqueId);
    }

    public void verifyChildDetails(Child child) throws JSONException {
        Button edit = solo.getButton("Edit");
        boolean isButtonPresent = edit != null;
        assertTrue(isButtonPresent);
        assertTrue(solo.searchText("Basic Identity"));
        assertTrue(solo.searchText(child.get("name").toString(),true));
        assertTrue(solo.searchText(child.get("unique_identifier").toString(),true));
    }

    public void testSortByName() {
      solo.clickOnMenuItem(solo.getString(R.string.sort_by));
      solo.waitForText(solo.getString(R.string.sort_by_name));
      solo.clickOnText(solo.getString(R.string.sort_by_name));
    }

    public void testSortByRecentUpdate() {
        solo.clickOnMenuItem(solo.getString(R.string.sort_by));
        solo.waitForText(solo.getString(R.string.sort_by_recent_update));
        solo.clickOnText(solo.getString(R.string.sort_by_recent_update));
    }
}
