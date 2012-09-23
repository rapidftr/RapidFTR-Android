package com.rapidftr;

import com.github.droidfu.DroidFuApplication;
import com.rapidftr.forms.ChildDetailsForm;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RapidFtrApplication extends DroidFuApplication {

    private static String formSectionsTemplate;

    private static boolean loggedIn;

    public static String getFormSectionsBody() {
        return formSectionsTemplate;
    }

    public static void setFormSectionsTemplate(String formSectionsTemplate) {
        RapidFtrApplication.formSectionsTemplate = formSectionsTemplate;
    }

    public static List<ChildDetailsForm> getChildFormSections() throws Exception{
        List<ChildDetailsForm> formList = Arrays.asList(new ObjectMapper().readValue(getFormSectionsBody(), ChildDetailsForm[].class));
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
