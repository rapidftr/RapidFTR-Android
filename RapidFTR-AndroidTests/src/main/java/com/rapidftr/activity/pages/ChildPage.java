package com.rapidftr.activity.pages;

import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;
import com.rapidftr.view.fields.TextField;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;

public class ChildPage {

    public Solo solo;
    int formPosition ;
    List automationFormData = Arrays.asList("Automation TextField value", "Automation TextArea value", "Check 1", "Select 1", "Radio 3", "1", "20", "10", "2012");
    private int nameHashCode = "name".hashCode();

    public ChildPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToRegisterPage() {
        solo.clickOnText("Register");
        solo.waitForText("Basic Identity");
    }

    public List<String> getDropDownFormSections() {

      solo.clickOnText("Basic Identity",0);
       solo.waitForText("Automation Form");
        ListAdapter adapter = solo.getCurrentListViews().get(0).getAdapter();
        int totalCount = adapter.getCount();
        List<String> formSections = new ArrayList<String>();
       for(int i=0;i<totalCount;i++){
           formSections.add(adapter.getItem(i).toString());
        }
        return formSections;
    }

    public void selectFormSection(String formSectionName) {
        solo.waitForText("Save");
        solo.clickOnView(solo.getCurrentSpinners().get(0));
        solo.waitForText(formSectionName);
        ListAdapter adapter= solo.getCurrentListViews().get(0).getAdapter();
        for(int i=0;i<adapter.getCount();i++){
            if(adapter.getItem(i).toString().equalsIgnoreCase(formSectionName)){
                 formPosition=i;
                break;
            }
        }
        solo.clickOnText(adapter.getItem(formPosition).toString());
        solo.waitForText(formSectionName);
        solo.sleep(3);
    }

    public void verifyFields(List fieldNames, boolean visible) {
        for (Object fieldName : fieldNames) {
            assertEquals(format("Visibility of field %s", fieldName),
                         visible, solo.searchText(fieldName.toString(), 1, true, true));
        }
    }

    public void registerChild(){
        enterAutomationFormDetails(automationFormData);
        save();
    }

    public void save() {
        solo.clickOnButton("Save");
        Assert.assertTrue(solo.waitForText("Saved record successfully"));
        solo.waitForText("Edit");
    }

    public void enterAutomationFormDetails(List automationFormData) {
        solo.enterText(0, automationFormData.get(0).toString());
        solo.sleep(3);
        solo.enterText(1, automationFormData.get(1).toString());
        int checkBoxCount=solo.getCurrentCheckBoxes().size();
        for(int i=0;i<checkBoxCount;i++){
            if (solo.getCurrentCheckBoxes().get(i).getText().toString().equals(automationFormData.get(2).toString())) {
                solo.waitForText(solo.getCurrentCheckBoxes().get(i).getText().toString(), 1,2000, true);
                solo.clickOnCheckBox(i);
            }
        }
        solo.scrollDown();
        Spinner spinner = solo.getCurrentSpinners().get(1);
        int selectBoxCount = spinner.getCount();
        for(int i=0;i<selectBoxCount;i++){
            if(spinner.getAdapter().getItem(i).toString().equals(automationFormData.get(3).toString())){
                solo.pressSpinnerItem(1,i);
//                solo.clickOnText(automationFormData.get(3).toString(),1,true);
            }
        }
        solo.clickOnText(automationFormData.get(4).toString(),1,true);
    }

    public void verifyRegisterChildDetail(List automationFormData,String formName) {
        solo.searchButton("Edit", true);
        selectFormSection(formName);
        Assert.assertTrue(solo.searchEditText(automationFormData.get(0).toString()));
        Assert.assertTrue(solo.searchEditText(automationFormData.get(1).toString()));
    }

    public void selectEditChild(){
        solo.waitForText("Edit");
        solo.clickOnText("Edit");
    }

    public void enterChildName(String name){
        solo.waitForText("Save");
        TextField textField = (TextField) solo.getCurrentActivity().findViewById(nameHashCode);
        EditText nameField = (EditText) textField.findViewById(R.id.value);
        solo.enterText(nameField, "");
        solo.enterText(nameField, name);
    }


    public void choosePopUpAction(String popUpAction) {
        solo.clickOnText(popUpAction);
    }

    public boolean verifyRegisterPopup() {

        return  solo.searchText("Cancel",true) && solo.searchText("Save",true) && solo.searchText("Discard",true) && solo.searchText("Choose an action",true);

    }
}