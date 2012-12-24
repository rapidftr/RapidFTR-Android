package com.rapidftr;

import android.app.Application;
import android.content.SharedPreferences;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rapidftr.forms.FormSection;
import com.rapidftr.task.SyncAllDataAsyncTask;
import com.rapidftr.utils.ApplicationInjector;
import com.rapidftr.utils.EncryptionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RapidFtrApplication extends Application {


    @RequiredArgsConstructor(suppressConstructorProperties = true)
    public enum Preference {
        USER_NAME("USER_NAME"),
        USER_ORG("USER_ORG"),
        SERVER_URL("SERVER_URL");

        private final @Getter String key;
    }

    public static final String SHARED_PREFERENCES_FILE = "RAPIDFTR_PREFERENCES";
    public static final String APP_IDENTIFIER = "RapidFTR";

    private static @Getter RapidFtrApplication applicationInstance;

    private @Getter final Injector injector;

    private @Getter @Setter List<FormSection> formSections;
    private @Getter @Setter boolean loggedIn;
    private @Getter @Setter String dbKey;
    private @Getter @Setter SyncAllDataAsyncTask syncTask;

    public RapidFtrApplication() {
        this(Guice.createInjector(new ApplicationInjector()));
    }

    public RapidFtrApplication(Injector injector) {
        RapidFtrApplication.applicationInstance = this;
        this.injector = injector;
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
    }

    public String getPreference(Preference preference) {
        return getSharedPreferences().getString(preference.getKey(), null);
    }

    public void setPreference(Preference preference, String value) {
        setPreference(preference.getKey(), value);
    }

    public void setPreference(String key, String value){
        getSharedPreferences().edit().putString(key, value).commit();
    }

    public void removePreference(Preference preference) {
        getSharedPreferences().edit().remove(preference.getKey());
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
