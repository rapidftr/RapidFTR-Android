package com.rapidftr.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.Preference.SERVER_URL;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(CustomTestRunner.class)
public class ApplicationInjectorTest {

    Injector injector;
    RapidFtrApplication application;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new ApplicationInjector());
        application = RapidFtrApplication.getApplicationInstance();
    }

    @Test
    public void testUserName() {
        application.setPreference(USER_NAME, "test");
        String result = injector.getInstance(Key.get(String.class, Names.named("USER_NAME")));
        assertThat(result, equalTo("test"));
    }

    @Test
    public void testDbName() {
        application.setPreference(SERVER_URL, "https://12.34.56.78:90");
        String result = injector.getInstance(Key.get(String.class, Names.named("DB_NAME")));
        assertThat(result, equalTo("12_34_56_78_90"));
    }

}
