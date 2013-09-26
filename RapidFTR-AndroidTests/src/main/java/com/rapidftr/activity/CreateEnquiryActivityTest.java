package com.rapidftr.activity;

import java.util.*;

import static java.util.Arrays.asList;

public class CreateEnquiryActivityTest extends BaseActivityIntegrationTest {

    private HashMap<String, List<String>> formSectionMap = new HashMap<String, List<String>>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        waitUntilTextDisappears("Login Successful");
        enquiryPage.navigateToCreatePage();
        addFormSection("Enquirer Details", "Name", "Sex", "Date of birth", "Place of birth", "Address", "Telephone number", "Ethnic group/tribe", "Nationality", "Relationship to child", "Message for the child");
        addFormSection("Child Details", "Name", "Also known as(nickname)", "Sex", "Date of birth/age", "Languages spoken", "Nationality", "Ethnic group/tribe", "Distinguishing physical characteristics");
        addFormSection("Family Details", "Father's name", "Is father alive?", "If father dead, please provide details", "Mother's name", "Is mother alive?", "If mother dead please provide details", "Address of child before separation");
        addFormSection("Siblings Details", "1) Name of sibling or other child accompanying the child", "Relationship", "Date of birth", "Place of birth", "Current address", "Telephone", "2) Name of sibling or other child accompanying the child", "Relationship", "Date of birth", "Place of birth", "Current address", "Telephone", "3) Name of sibling or other child accompanying the child", "Relationship", "Date of birth", "Place of birth", "Current address", "Telephone");
        addFormSection("Separation History", "Date of separation", "Place of separation", "Circumstances of separation");
        addFormSection("Tracing Information", "Latest news received");
    }

    private void addFormSection(String formSectionName, String... formSectionFields) {
        this.formSectionMap.put(formSectionName, new ArrayList<String>(asList(formSectionFields)));
    }

    public void testFormSectionsAvailableForDisplay(){
        Set<String> actualSections = new HashSet<String>(enquiryPage.getAllFormSections());
        Set<String> expectedSections = this.formSectionMap.keySet();

        assertTrue(String.format("Actual %s\n Expected %s", actualSections, expectedSections), actualSections.equals(expectedSections));
    }

    public void testFormFieldsDisplayed(){
        for (Map.Entry<String, List<String>> formSection : formSectionMap.entrySet()) {
            enquiryPage.selectFormSection(formSection.getKey());
            enquiryPage.verifyFieldsDisplayed(formSection.getValue());
        }
    }

    public void testEnquirerNameValidation(){
        List<String> enquirerDetails = asList("");
        enquiryPage.enterEnquirerDetails(enquirerDetails);
        enquiryPage.save();
        enquiryPage.assertPresenceOfValidationMessage();
    }

    public void testSaveEnquiryWithFamilyDetails() throws Exception {
        List<String> enquirerDetails = asList("Rajni");
        enquiryPage.enterEnquirerDetails(enquirerDetails);
        List<String> familyDetails = asList("Mother");
        enquiryPage.selectFormSection("Family Details");
        enquiryPage.enterFamilyDetails(familyDetails);
        enquiryPage.save();
        enquiryPage.verifyNewEnquiryFormPresence();

    }

    public void testAfterSaveShouldShowNewForm() throws Exception {
        List<String> enquirerDetails = asList("Rajni");
        enquiryPage.enterEnquirerDetails(enquirerDetails);
        enquiryPage.save();
        enquiryPage.verifyNewEnquiryFormPresence();
    }
}
