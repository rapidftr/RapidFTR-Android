package com.rapidftr;

import android.app.Application;
import android.content.Context;
import com.rapidftr.forms.FormSection;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RapidFtrApplication extends Application {

    private static String formSectionsTemplate;
    private static boolean loggedIn;
    private static String dbKey;
    private static RapidFtrApplication instance;

    public RapidFtrApplication(){
        instance = this;
    }

    public static String getFormSectionsBody() {
        return formSectionsTemplate;
    }

    public static void setFormSectionsTemplate(String formSectionsTemplate) {
        RapidFtrApplication.formSectionsTemplate = formSectionsTemplate;
    }

    public static List<FormSection> getChildFormSections() throws Exception{
        List<FormSection> formList = Arrays.asList(new ObjectMapper().readValue(getFormSectionsBody(), FormSection[].class));
        Collections.sort(formList);
        return formList;
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
