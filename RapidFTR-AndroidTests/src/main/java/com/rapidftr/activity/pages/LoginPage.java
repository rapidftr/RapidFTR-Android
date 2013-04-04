package com.rapidftr.activity.pages;


import android.view.View;
import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;

public class LoginPage {

    public static final String HOST = "https://test.rapidftr.com";
//    public static final String PORT = "5001";
    public static final String LOGIN_URL = HOST ;
    public static final String PASSWORD = "admin";
    public static final String USERNAME = "admin";

    public final Solo solo;

    public LoginPage(Solo solo){
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
        solo.clickOnMenuItem(solo.getString(R.string.log_out));
        solo.waitForText("You have been logged out successfully.");
    }

    public void clickLoginButton() {
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

    public String getPasswordRequiredMessage(){
        solo.clickOnEditText(1);
        return ((EditText)solo.getCurrentActivity().findViewById(R.id.password)).getError().toString();
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
}
