package com.rapidftr.activity.pages;

import android.widget.Button;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.model.Child;
import org.json.JSONException;

import static junit.framework.Assert.assertTrue;

public class ViewAllChildrenPage {
    public Solo solo;
    int formPosition ;

    public ViewAllChildrenPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToViewAllPage() {
        solo.clickOnText("View All Children");
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
        assertTrue(solo.searchText(child.get("name").toString()));
        assertTrue(solo.searchText(child.get("unique_identifier").toString()));
    }
}
