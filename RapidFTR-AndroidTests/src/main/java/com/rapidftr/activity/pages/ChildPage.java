package com.rapidftr.activity.pages;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.jayway.android.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.List;

public class ChildPage {

    public Solo solo;
    int formPosition ;

    public ChildPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToRegisterPage() {
        solo.clickOnText("Register Child");
        solo.waitForText("Basic Identity");
    }

    public List<String> getDropDownFormSections() {

      solo.clickOnText("Basic Identity",0);
       solo.waitForText("Automation Form");
        ListAdapter adapter = solo.getCurrentListViews().get(0).getAdapter();
        int totalCount = adapter.getCount();
        ArrayList formSections = new ArrayList();
       for(int i=0;i<totalCount;i++){
           formSections.add(adapter.getItem(i).toString());
        }
//        solo.getCurrentListViews().get(0).getAdapter().getItem(2);
//        Spinner spinner= (Spinner)solo.getCurrentSpinners().get(0).getSelectedItem();
        return formSections;
    }


    public int getIndexFromElement(ArrayAdapter adapter, String element) {
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(element)) {
                return i;
            }
        }
        return 0;
    }


    public void selectFormSection(String formSectionName) {
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


    }

    public boolean verifyFields(List fieldNames) {
        boolean result = true;
        for(int i=0; i<fieldNames.size();i++){
            if(solo.searchText(fieldNames.get(i).toString(),true)==false){
                result=false;
                Log.e(fieldNames.get(i).toString()," Field Does not exists in the Form ");
                break;
            }
        }
        return result;
    }


    public void save() {
        solo.clickOnButton("Save");
        solo.waitForText("Saved child record successfully");
    }

    public void enterAutomationFormDetails(List automationFormData) {
        solo.enterText(0, automationFormData.get(0).toString());
        solo.enterText(1, automationFormData.get(1).toString());
        int checkBoxCount=solo.getCurrentCheckBoxes().size();
        for(int i=0;i<checkBoxCount;i++){
        if (solo.getCurrentCheckBoxes().get(i).getText().toString().equals(automationFormData.get(2).toString())){
            solo.clickOnCheckBox(i);
        }
        }
        int selectBoxCount=solo.getCurrentSpinners().get(1).getCount();

        for(int i=0;i<selectBoxCount;i++){
            if(solo.getCurrentSpinners().get(1).getAdapter().getItem(i).toString().equals(automationFormData.get(3).toString())){
                solo.pressSpinnerItem(1,i);
                solo.clickOnText(automationFormData.get(3).toString());
            }
        }

        solo.clickOnText(automationFormData.get(2).toString());
        solo.clickOnText(automationFormData.get(4).toString());
//        solo.enterText(2,automationFormData.get(5).toString());




    }

    public void verifyRegisterChildDetail(List automationFormData,String formName) {
        solo.searchButton("Edit",true);
        selectFormSection("formName");
        solo.isTextChecked(automationFormData.get(0).toString());
        solo.isTextChecked(automationFormData.get(1).toString());
        solo.isCheckBoxChecked(automationFormData.get(2).toString());
        solo.isRadioButtonChecked(automationFormData.get(3).toString());
//        solo.getCurrentNumberPickers().get(0) ;
//        solo.getCurrentDatePickers().get(0);

    }
}