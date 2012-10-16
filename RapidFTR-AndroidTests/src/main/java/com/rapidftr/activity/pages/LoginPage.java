package com.rapidftr.activity.pages;


import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.rapidftr.R;

public class LoginPage {

    public static void login(String username, String password, String url) {
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById(R.id.username), "");
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById((R.id.username)), username);

        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById(R.id.password), "");
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById((R.id.password)), password);
        View linkView = Page.solo.getCurrentActivity().findViewById(R.id.change_url);
        if (View.VISIBLE == linkView.getVisibility()) {
            changeURL();
        }
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById(R.id.url), "");
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById(R.id.url), url);
        clickLoginButton();
    }


    public static void logout() {
        System.out.println(Page.solo.searchButton("Log Out"));
        Page.solo.clickOnButton("Log Out");
    }

    public static void clickLoginButton() {
        System.out.println(Page.solo.searchButton("Log In"));
        Page.solo.clickOnButton("Log In");
    }

    public static void changeURL() {
        Page.solo.clickOnText("Change URL");
    }

    public static String getUrl(){
        return ((EditText)Page.solo.getCurrentActivity().findViewById(R.id.url)).getText().toString();


    }

    public static String getUserNameRequiredMessage(){
        Page.solo.clickOnEditText(0);
        return ((EditText)Page.solo.getCurrentActivity().findViewById(R.id.username)).getError().toString();

    }

    public static String getPasswordRequiredMEssage(){
        Page.solo.clickOnEditText(1);
        return ((EditText)Page.solo.getCurrentActivity().findViewById(R.id.password)).getError().toString();
    }

    public static String getURLRequiredMessage(){
        Page.solo.clickOnEditText(2);
        return ((EditText)Page.solo.getCurrentActivity().findViewById(R.id.url)).getError().toString();
    }
}
