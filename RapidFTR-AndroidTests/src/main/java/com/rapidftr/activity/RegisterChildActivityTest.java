package com.rapidftr.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterChildActivityTest extends BaseActivityIntegrationTest<LoginActivity> {

//    public RegisterChildPage registerChildPage;

    public RegisterChildActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        registerChildPage.navigateToRegisterPage();
}
    @Override
    public void tearDown() throws  Exception{
        solo.goBackToActivity("MainActivity");
        loginPage.logout();
        loginPage.clickLoginButton();
        super.tearDown();
    }


    public void testFormSectionsDisplayed() {
       List<String> actualSections = registerChildPage.getDropDownFormSections();
       List<String> expectedSections = new ArrayList<String>(Arrays.asList(new String[] { "Basic Identity", "Family details", "Care Arrangements", "Separation History", "Protection Concerns",
                                                             "Childs Wishes", "Other Interviews", "Other Tracing Info", "Interview Details", "Automation Form" }));
        assertEquals(actualSections, expectedSections);
    }


    public void testFieldsDisplayed() {
        registerChildPage.selectFormSection("Automation Form");
        List expectedFields = Arrays.asList(new String[] { "Automation TextField", "Automation TextArea", "Automation CheckBoxes", "Automation Select",
                "Automation Radio", "Automation Number", "Automation Date" });
        assertTrue(registerChildPage.verifyFields(expectedFields));
    }

    public void testFieldsHidden() {
        registerChildPage.selectFormSection("Automation Form");
        List hiddenField=Arrays.asList(new String[]{"Hidden TextField"});
        assertFalse(registerChildPage.verifyFields(hiddenField));

    }


    public void testRegisterChild() {
        registerChildPage.selectFormSection("Automation Form");
        List automationFormData = Arrays.asList(new String[] { "Automation TextField value", "Automation TextArea value","Check 3", "Select 1", "Radio 3","1","20","10","2012" });
        registerChildPage.enterAutomationFormDetails(automationFormData);
        registerChildPage.save();
        registerChildPage.verifyRegisterChildDetail(automationFormData,"Automation Form");
    }

    public void testEditChild(){
        String name="Test Edit Child";
        registerChildPage.selectFormSection("Automation Form");
        registerChildPage.registerChild();
        registerChildPage.selectEditChild();
        registerChildPage.selectFormSection("Basic Identity");
        registerChildPage.enterChildName(name);
        registerChildPage.save();
        solo.waitForText("Saved Child Record Successfully");
        assertTrue(registerChildPage.getChildName(name));
    }

    public void estSearchChild(){

    }

}
