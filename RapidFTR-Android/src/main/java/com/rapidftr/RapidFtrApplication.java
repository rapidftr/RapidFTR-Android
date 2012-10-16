package com.rapidftr;

import android.app.Application;
import com.rapidftr.forms.FormSection;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RapidFtrApplication extends Application {

    private static String formSectionsTemplate;

    private static boolean loggedIn;

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

}
