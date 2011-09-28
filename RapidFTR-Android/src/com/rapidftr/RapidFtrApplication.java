package com.rapidftr;

import com.rapidftr.forms.ChildDetailsForm;
import org.codehaus.jackson.map.ObjectMapper;
import com.github.droidfu.DroidFuApplication;

public class RapidFtrApplication extends DroidFuApplication {

    private static String formSectionsTemplate;

    private static boolean loggedIn;

    public static String getFormSectionsBody() {
        return formSectionsTemplate;
    }

    public static void setFormSectionsTemplate(String formSectionsTemplate) {
        RapidFtrApplication.formSectionsTemplate = formSectionsTemplate;
    }

    public static ChildDetailsForm[] getChildFormSections() throws Exception{
        return new ObjectMapper().readValue(getFormSectionsBody(), ChildDetailsForm[].class);
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        RapidFtrApplication.loggedIn = loggedIn;
    }
}
