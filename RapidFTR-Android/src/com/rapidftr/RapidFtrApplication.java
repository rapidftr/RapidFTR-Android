package com.rapidftr;

public class RapidFtrApplication {

    private static String formSectionsBody;

    private static boolean loggedIn;

    public static String getFormSectionsBody() {
        return formSectionsBody;
    }

    public static void setFormSectionsBody(String formSectionsBody) {
        RapidFtrApplication.formSectionsBody = formSectionsBody;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        RapidFtrApplication.loggedIn = loggedIn;
    }
}
