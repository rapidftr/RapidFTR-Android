package com.rapidftr;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Modules;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.User;
import com.rapidftr.utils.ApplicationInjector;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.Setup;

import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomTestRunner extends RobolectricTestRunner {

    public static List<FormSection> formSectionSeed = Arrays.asList(
        new FormSection(new HashMap<String, String>(){{put("en", "Section 1");}}, 1, true, new HashMap<String, String>(){{put("en", "Section Help 1");}}, Arrays.asList(
            new FormField("f1", null, true, "text_field", new HashMap<String, String>(){{put("en", "Field 1");}}, new HashMap<String, String>(){{put("en", "Help 1");}}, null, null),
            new FormField("f2", null, true, "textarea", new HashMap<String, String>(){{put("en", "Field 2");}}, new HashMap<String, String>(){{put("en", "Help 2");}}, null, null),
            new FormField("f3", null, true, "numeric_field", new HashMap<String, String>(){{put("en", "Field 3");}}, new HashMap<String, String>(){{put("en", "Help 3");}}, null, null)
        )),
            new FormSection(new HashMap<String, String>(){{put("en", "Section 2");}}, 1, true, new HashMap<String, String>(){{put("en", "Section Help 2");}}, Arrays.asList(
            new FormField("f4", null, true, "radio_button", new HashMap<String, String>(){{put("en", "Field 4");}}, new HashMap<String, String>(){{put("en", "Help 4");}}, new HashMap<String, List<String>>(){{put("en", Arrays.asList("radio1", "radio2", "radio3"));}}, null),
            new FormField("f5", null, true, "check_boxes", new HashMap<String, String>(){{put("en", "Field 5");}}, new HashMap<String, String>(){{put("en", "Help 5");}}, new HashMap<String, List<String>>(){{put("en", Arrays.asList("check1", "check2", "check3"));}}, null),
            new FormField("f6", null, true, "date_field", new HashMap<String, String>(){{put("en", "Field 6");}}, new HashMap<String, String>(){{put("en", "Help 6");}}, null, null)
        ))
    );

    public static class TestInjector extends AbstractModule {
        @Override
        protected void configure() {
        }
    }

    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        MockitoAnnotations.initMocks(testClass);
        Security.addProvider(new BouncyCastleProvider());
    }

	private static long userId = 0;

	public static User createUser() {
		return createUser("user" + (++userId));
	}

	public static User createUser(String userName) {
		return new User(userName, "testPassword", true, "localhost:3000", "testDbKey", "Test Organisation", "Test Name", "testPassword", "en");
	}

    @Override
    public Setup createSetup() {
        return super.createSetup();
    }
}
