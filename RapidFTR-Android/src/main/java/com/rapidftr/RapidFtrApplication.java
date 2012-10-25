package com.rapidftr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.forms.FormSection;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RapidFtrApplication extends Application {

    private static List<FormSection> formSectionsTemplate;
    private static boolean loggedIn;
    private static String dbKey;
    private static RapidFtrApplication instance;

    public RapidFtrApplication(){
        instance = this;
    }

    public static String getUserName() {
        SharedPreferences rapidftrPreferences = getContext().getSharedPreferences(RapidFtrActivity.SHARED_PREFERENCES_FILE,0);
        return rapidftrPreferences.getString("USER_NAME", null);
    }

    public static void setFormSectionsTemplate(String formSectionResponse) throws IOException {
        formSectionsTemplate = Arrays.asList(new ObjectMapper().readValue(formSectionResponse, FormSection[].class));
        Collections.sort(formSectionsTemplate);

        ListIterator<FormSection> iterator = formSectionsTemplate.listIterator();
        while (iterator.hasNext())
            if (!iterator.next().isEnabled())
                iterator.remove();
    }

    public static List<FormSection> getChildFormSections() {
        return formSectionsTemplate;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        RapidFtrApplication.loggedIn = loggedIn;
    }

    public static String getDbKey(){
        return dbKey;
    }

    public static void setDbKey(String dbKey) {
        RapidFtrApplication.dbKey = dbKey;
    }

    public static Context getContext() {
        return instance;
    }
}
