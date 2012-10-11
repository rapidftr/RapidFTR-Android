package com.rapidftr.activity.pages;


import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.rapidftr.R;

public class LoginPage {

    public static void login(String username, String password, String url) {
        Page.solo.enterText((EditText)Page.solo.getCurrentActivity().findViewById((R.id.username)),username);
        Page.solo.enterText((EditText) Page.solo.getCurrentActivity().findViewById((R.id.password)), password);
        View linkView = Page.solo.getCurrentActivity().findViewById(R.id.change_url);
            if(View.VISIBLE==linkView.getVisibility())
            {
                    changeURL();
            }
        Page.solo.enterText((EditText)Page.solo.getCurrentActivity().findViewById(R.id.url),"");
        Page.solo.enterText((EditText)Page.solo.getCurrentActivity().findViewById(R.id.url),url);
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
}
