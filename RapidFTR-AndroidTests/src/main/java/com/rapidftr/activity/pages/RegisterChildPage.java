package com.rapidftr.activity.pages;

import com.jayway.android.robotium.solo.Solo;

import java.util.List;

public class RegisterChildPage {

    public Solo solo;

    public RegisterChildPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToRegisterPage() {
        solo.clickOnText("Register Child");
    }

    public List<String> getDropDownFormSections() {
      solo.clickOnText("Basic Identity");
      List availableSpinner = solo.getCurrentSpinners();
      availableSpinner.get(0).toString();
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void selectFormSection(String s) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public List<String> getFieldLabels() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void save() {
        //To change body of created methods use File | Settings | File Templates.
    }
}
