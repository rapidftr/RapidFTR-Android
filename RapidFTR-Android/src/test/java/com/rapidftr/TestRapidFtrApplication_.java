package com.rapidftr;

import org.junit.Ignore;
import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

@Ignore
public class TestRapidFtrApplication_ extends RapidFtrApplication implements TestLifecycleApplication {

    public TestRapidFtrApplication_() {
        super(CustomTestRunner.INJECTOR);
    }

    @Override
    public void beforeTest(Method method) {
        try {
            getSharedPreferences().edit().putString(SERVER_URL_PREF, "http://1.2.3.4:5");
            setFormSections(CustomTestRunner.formSectionSeed);
            setCurrentUser(CustomTestRunner.createUser());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void prepareTest(Object o) {
    }

    @Override
    public void afterTest(Method method) {
    }
}
