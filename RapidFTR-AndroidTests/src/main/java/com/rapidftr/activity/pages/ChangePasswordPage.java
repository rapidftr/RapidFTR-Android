package com.rapidftr.activity.pages;


import android.view.View;
import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;

public class ChangePasswordPage {

    public static final String HOST = "https://test.rapidftr.com";
    //    public static final String PORT = "5001";
    public static final String LOGIN_URL = HOST ;
    public static final String PASSWORD = "rapidftr";
    public static final String USERNAME = "rapidftr";

    public final Solo solo;

    public ChangePasswordPage(Solo solo){
        this.solo = solo;
    }

    public void changePassword(String password, String newPassword, String confirmPassword) {
        solo.waitForActivity("ChangePasswordActivity") ;
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.current_password), password);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById((R.id.new_password)), newPassword);
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.new_password_confirm), confirmPassword);
        solo.clickOnButton("Change Password");
    }

    public String getCurrentPasswordRequiredMessage(){
        solo.clickOnEditText(0);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.current_password)).getError().toString();

    }

    public String getNewPasswordRequiredMessage(){
        solo.clickOnEditText(1);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.new_password)).getError().toString();

    }

    public String getNewPasswordConfirmRequiredMessage(){
        solo.clickOnEditText(2);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.new_password_confirm)).getError().toString();

    }
}