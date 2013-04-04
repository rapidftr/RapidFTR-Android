package com.rapidftr.activity.pages;


import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;

public class SearchChildrenPage {
    public Solo solo;

    public SearchChildrenPage(Solo solo) {
        this.solo=solo;
    }


    public void navigateToSearchTab(){
        solo.clickOnText("Search");
        solo.waitForActivity("SearchActivity");
    }
    public void searchChild(String childName) {
        solo.enterText((EditText)solo.getCurrentActivity().findViewById(R.id.search_text),childName);
        clickSearch();
    }

    public void clickSearch() {
        solo.clickOnButton("Go");
    }

    public boolean isChildPresent(String uniqueId, String childName) {
            return solo.searchText(uniqueId,true) && solo.searchText(childName,true);
    }

//    public boolean isTextPresent(String childName) {
//        return solo.searchText(childName,true);
//    }
}
