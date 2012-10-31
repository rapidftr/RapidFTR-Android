package com.rapidftr.activity;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class RegisterChildActivityTest extends BaseActivityIntegrationTest<RegisterChildActivity> {

//    public RegisterChildPage registerChildPage;

    public RegisterChildActivityTest() {
        super(RegisterChildActivity.class);
    }

//    @Override
//    public void setUp() throws Exception {
////        super.setUp();
//
//    }

    public void testFormSectionsDisplayed() {
        loginPage.login(loginPage.USERNAME,loginPage.PASSWORD,loginPage.LOGIN_URL);
        registerChildPage.navigateToRegisterPage();
        List<String> actualSections = registerChildPage.getDropDownFormSections();
        List expectedSections = Arrays.asList(new String[] { "Basic Identity", "Family Details", "Care Arrangements", "Separation History", "Protection Concerns",
                                                             "Childs Wishes", "Other Interviews", "Other Tracing Info", "Interview Details", "Automation Form" });

        assertThat(actualSections, equalTo(expectedSections));
    }

    public void testFormSectionHidden() {
        List<String> actualSections = registerChildPage.getDropDownFormSections();
        assertThat(actualSections, not(hasItem("Automation Hidden Form")));
    }

    public void testFieldsDisplayed() {
        registerChildPage.selectFormSection("Automation Form");
        List<String> actualFields = registerChildPage.getFieldLabels();
        List expectedFields = Arrays.asList(new String[] { "Automation TextField", "Automation TextArea", "Automation CheckBoxes", "Automation Select",
                                                             "Automation Radio", "Automation Number", "Automation Date" });

        assertThat(actualFields, equalTo(expectedFields));
    }

    public void testFieldsHidden() {
        List<String> actualSections = registerChildPage.getFieldLabels();
        assertThat(actualSections, not(hasItem("Hidden TextField")));
    }

    public void testRegisterChild() {
        registerChildPage.selectFormSection("Automation Form");
        registerChildPage.save();
    }

}
