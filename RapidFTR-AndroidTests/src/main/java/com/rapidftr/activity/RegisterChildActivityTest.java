package com.rapidftr.activity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class RegisterChildActivityTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        waitUntilTextDisappears("Login Successful");
        childPage.navigateToRegisterPage();
    }

    public void testFormSectionsDisplayed() {
        List<String> actualSections = childPage.getDropDownFormSections();
        List<String> expectedSections = new ArrayList<String>(asList(new String[]{"Basic Identity", "Family details", "Care Arrangements", "Separation History", "Protection Concerns",
                "Childs Wishes", "Other Interviews", "Other Tracing Info", "Interview Details", "Photos and Audio","Automation Form", "Feedback"}));
        assertTrue(actualSections.equals(expectedSections));
        solo.goBack();
    }

    public void testFieldsDisplayed() {
        childPage.selectFormSection("Automation Form");
        List expectedFields = asList("Automation TextField", "Automation TextArea", "Automation CheckBoxes", "Automation Select",
                "Automation Radio", "Automation Number", "Automation Date");
        childPage.verifyFields(expectedFields, true);
    }

    public void testFieldsHidden() {
        childPage.selectFormSection("Automation Form");
        List hiddenField = asList("Hidden TextField");
        childPage.verifyFields(hiddenField, false);
    }

    public void testRegisterChild() {
        childPage.selectFormSection("Automation Form");
        List automationFormData = Arrays.asList("Automation TextField value", "Automation TextArea value", "Check 1", "Select 1", "Radio 3", "1", "20", "10", "2012");
        childPage.enterAutomationFormDetails(automationFormData);
        childPage.save();
        childPage.verifyRegisterChildDetail(automationFormData, "Automation Form");
    }

    public void testRegisterAndSyncChild() {
        childPage.selectFormSection("Automation Form");
        List automationFormData = asList("Automation TextField value", "Automation TextArea value", "Check 3", "Select 1", "Radio 3", "1", "20", "10", "2012");
        childPage.enterAutomationFormDetails(automationFormData);
        childPage.save();
        childPage.verifyRegisterChildDetail(automationFormData, "Automation Form");
    }

    public void testEditChild() {
        String name = "Test Edit Child";
        childPage.selectFormSection("Automation Form");
        childPage.registerChild();
        childPage.selectEditChild();
        childPage.selectFormSection("Basic Identity");
        childPage.enterChildName(name);
        childPage.save();
        solo.waitForText("Saved Record Successfully");
        assertTrue(isEditedTextPresent(name));
    }

    public void testNavigatingAwayFromRegisterPromptUserToSave() {
        String name = "MsgPromptText";
        childPage.enterChildName(name);
        loginPage.logout();
        assertTrue(childPage.verifyRegisterPopup());
        childPage.choosePopUpAction("Cancel");
        assertTrue(solo.waitForText(name));
        searchPage.navigateToSearchTab();
        assertTrue(childPage.verifyRegisterPopup());
        childPage.choosePopUpAction("Save");
        childPage.selectEditChild();
        viewAllChildrenPage.navigateToViewAllTab();
        assertTrue(childPage.verifyRegisterPopup());
        childPage.choosePopUpAction("Discard");
    }
}
