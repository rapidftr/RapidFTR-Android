package com.rapidftr;

import android.app.Application;
import android.content.SharedPreferences;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
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

    private @Getter static RapidFtrApplication instance;

    private @Getter final Injector injector;

    private @Getter @Setter List<FormSection> formSections;
    private @Getter @Setter boolean loggedIn;
    private @Getter @Setter String dbKey;

    public RapidFtrApplication() {
        this(Guice.createInjector(new ApplicationInjector()));
    }

    public RapidFtrApplication(Injector injector) {
        RapidFtrApplication.instance = this;
        this.injector = injector;
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
