package com.rapidftr.activity.pages;

import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;

import java.util.ArrayList;
import java.util.List;

public class UnverifiedUserPage {
        public final Solo solo;

    public UnverifiedUserPage(Solo solo) {
        this.solo = solo;
    }

    public boolean verifySyncLocationPopUp() {

       return solo.searchText("Enter sync location",true) && solo.searchText("Please enter the the location you wish to synchronise with",true);
    }

    public void enterSyncLocationAndStartSync(String syncUrl) {
        solo.enterText(solo.getEditText(0),syncUrl);
        solo.clickOnButton("Ok");
        solo.sleep(10000);
    }

    public void clickSignUpLink() {
        solo.clickOnText(solo.getString(R.string.signup));
        solo.waitForText("All fields are mandatory");
    }

    public void registerUnverifiedUser(String userName, String password, String reEnterPassword, String fullName, String organisation){
        solo.sleep(5);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.username)," ");
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.username),userName);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.password),password);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.confirm_password),reEnterPassword);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.full_name),fullName);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.organisation),organisation);
        solo.clickOnButton(solo.getString(R.string.signup));
    }

    public String getUserNameRequiredMessage(){
        solo.clickOnEditText(0);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.username)).getError().toString();

    }

    public List<String> getFieldsErrorMessages(){
        List<String> errorMessages = new ArrayList<String>();
        String userNameErrorMessage=((EditText)solo.getCurrentActivity().findViewById(R.id.username)).getError().toString();
        errorMessages.add(userNameErrorMessage);
        String passwordErrorMessage=((EditText)solo.getCurrentActivity().findViewById(R.id.password)).getError().toString();
        errorMessages.add(passwordErrorMessage);
        String resetPasswordErrorMessage=((EditText)solo.getCurrentActivity().findViewById(R.id.confirm_password)).getError().toString();
        errorMessages.add(resetPasswordErrorMessage);
        String fullNameErrorMessage=((EditText)solo.getCurrentActivity().findViewById(R.id.full_name)).getError().toString();
        errorMessages.add(fullNameErrorMessage);
        String organisation=((EditText)solo.getCurrentActivity().findViewById(R.id.organisation)).getError().toString();
        errorMessages.add(organisation);
        return errorMessages;
    }




}

