package com.rapidftr;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.google.inject.Guice;
import com.google.inject.Injector;
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

public class RapidFtrApplication extends Application implements Thread.UncaughtExceptionHandler {

    public static final String SHARED_PREFERENCES_FILE = "RAPIDFTR_PREFERENCES";
    public static final String APP_IDENTIFIER = "RapidFTR";

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
        return getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
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

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(APP_IDENTIFIER, throwable.getMessage(), throwable);
        Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show();
    }
}
