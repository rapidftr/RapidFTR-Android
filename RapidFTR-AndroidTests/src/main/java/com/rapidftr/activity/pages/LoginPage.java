package com.rapidftr.activity.pages;


import android.widget.EditText;
import com.rapidftr.R;

public class LoginPage {

    public static void login(String username, String password, String url) {

        Page.solo.enterText((EditText)Page.solo.getCurrentActivity().findViewById((R.id.username)),username);
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById((R.id.password)), password);
            if (Page.solo.searchText("Change URL") == true ){
                    changeURL();
            }
            Page.solo.enterText((EditText)Page.solo.getCurrentActivity().findViewById(R.id.base_url),"");
            Page.solo.enterText((EditText)Page.solo.getCurrentActivity().findViewById(R.id.base_url),url);
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
}
