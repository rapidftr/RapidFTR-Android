package com.rapidftr;

import android.app.Application;
import android.content.SharedPreferences;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.forms.FormSection;
import com.rapidftr.utils.ApplicationInjector;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RapidFtrApplication extends Application {

    private static RapidFtrApplication instance;

    public void setFormSections(List<FormSection> formSections) {
        this.formSections = formSections;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public Injector getInjector() {

        return injector;
    }

    public List<FormSection> getFormSections() {
        return formSections;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getDbKey() {
        return dbKey;
    }

    private final Injector injector = Guice.createInjector(new ApplicationInjector());

    private List<FormSection> formSections;
    private boolean loggedIn;
    private String dbKey;

    public RapidFtrApplication(){
        instance = this;
    }

    public static RapidFtrApplication getContext() {
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(RapidFtrActivity.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
    }

    public String getUserName() {
        return getSharedPreferences().getString("USER_NAME", null);
    }

    public void setUserName(String userName) {
        getSharedPreferences().edit().putString("USER_NAME", userName).commit();
    }

    public void setFormSectionsTemplate(String formSectionResponse) throws IOException {
        formSections = Arrays.asList(new ObjectMapper().readValue(formSectionResponse, FormSection[].class));
        Collections.sort(formSections);

        ListIterator<FormSection> iterator = formSections.listIterator();
        while (iterator.hasNext())
            if (!iterator.next().isEnabled())
                iterator.remove();
    }

}
