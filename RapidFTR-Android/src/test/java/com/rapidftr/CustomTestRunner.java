package com.rapidftr;

import android.app.Application;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Modules;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.utils.ApplicationInjector;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.Preference.SERVER_URL;
import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;

public class CustomTestRunner extends RobolectricTestRunner {

    public static List<FormSection> formSectionSeed = Arrays.asList(
        new FormSection("Section 1", 1, true, "Section Help 1", Arrays.asList(
            new FormField("f1", true, null, true, "text_field", "Field 1", "Help 1", null, null),
            new FormField("f2", true, null, true, "textarea", "Field 2", "Help 2", null, null),
            new FormField("f3", true, null, true, "numeric_field", "Field 3", "Help 3", null, null)
        )),
        new FormSection("Section 2", 2, true, "Section Help 2", Arrays.asList(
            new FormField("f4", true, null, true, "radio_button", "Field 4", "Help 4", Arrays.asList("radio1", "radio2", "radio3"), null),
            new FormField("f5", true, null, true, "check_boxes", "Field 5", "Help 5", Arrays.asList("check1", "check2", "check3"), null),
            new FormField("f6", true, null, true, "date_field", "Field 6", "Help 6", null, null)
        ))
    );

    public static class TestInjector extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Provides
        public DatabaseHelper getDatabaseHelper() {
            return ShadowSQLiteHelper.getInstance();
        }
    }

    public static Injector INJECTOR = Guice.createInjector(Modules.override(new ApplicationInjector()).with(new TestInjector()));

    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        MockitoAnnotations.initMocks(testClass);
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    protected Application createApplication() {
        RapidFtrApplication application = new RapidFtrApplication(INJECTOR);
        application.setPreference(SERVER_URL, "http://1.2.3.4:5");
        application.setPreference(USER_NAME, "user1");
        application.setDbKey("db_key");
        application.setFormSections(formSectionSeed);
        return application;
    }
}
