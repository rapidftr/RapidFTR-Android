package com.rapidftr.service;

import android.widget.Toast;
import com.rapidftr.RapidFtrApplication;

import static android.widget.Toast.LENGTH_LONG;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;

public class LogOutService {

    public void logOut(RapidFtrApplication context) {
        context.setLoggedIn(false);
        context.removePreference(USER_NAME);
        context.removePreference(USER_ORG);
        Toast.makeText(context, "You have been logged out.", LENGTH_LONG).show();
    }
}
