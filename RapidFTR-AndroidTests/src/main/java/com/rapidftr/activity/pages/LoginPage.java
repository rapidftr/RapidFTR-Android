package com.rapidftr.activity.pages;


import android.view.View;
import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;

public class LoginPage {

    public static final String LOGIN_URL = "dev.rapidftr.com:3000";
    public static final String PASSWORD = "rapidftr";
    public static final String USERNAME = "rapidftr";

    public final Solo solo;

    public LoginPage(Solo solo) {
        this.solo = solo;
    }

    public void login() {
        login(USERNAME, PASSWORD, LOGIN_URL);
    }

    public void login(String username, String password, String url) {
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.username), "");
        solo.enterText((EditText) solo.getCurrentActivity().findViewById((R.id.username)), username);

        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.password), "");
        solo.enterText((EditText) solo.getCurrentActivity().findViewById((R.id.password)), password);
        View linkView = solo.getCurrentActivity().findViewById(R.id.change_url);
        if (View.VISIBLE == linkView.getVisibility()) {
            changeURL();
        }
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.url), "");
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.url), url);
        clickLoginButton();
    }


    public void logout() {
        System.out.println(solo.searchButton("Log Out"));
        solo.clickOnButton("Log Out");
    }

    public void clickLoginButton() {
        System.out.println(solo.searchButton("Log In"));
        solo.clickOnButton("Log In");
    }

    public void changeURL() {
        solo.clickOnText("Change URL");
    }

    public String getUrl(){
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.url)).getText().toString();
    }

    public String getUserNameRequiredMessage(){
        solo.clickOnEditText(0);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.username)).getError().toString();

    }

    public String getPasswordRequiredMEssage(){
        solo.clickOnEditText(1);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.password)).getError().toString();
    }

    public String getURLRequiredMessage(){
        solo.clickOnEditText(2);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.url)).getError().toString();
    }
}
