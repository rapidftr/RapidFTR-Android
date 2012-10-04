package com.rapidftr.activity.pages;

import com.jayway.android.robotium.solo.Solo;

public class Page {
    public static void setSolo(Solo solo) {
        Page.solo = solo;
    }

    public static Solo solo;
    public static LoginPage loginPage;


}
