package com.rapidftr.service;

import com.rapidftr.RapidFtrApplication;

import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;

public class LogOutService {

    public void logOut(RapidFtrApplication context) {
        context.setLoggedIn(false);
        context.removePreference(USER_NAME);
        context.removePreference(USER_ORG);
    }
}
