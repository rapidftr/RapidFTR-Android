package com.rapidftr;

import android.content.SharedPreferences;
import com.rapidftr.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.rapidftr.CustomTestRunner.createUser;
import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class RapidFtrApplicationTest {

    private RapidFtrApplication application;

    @Before
    public void setUp() {
        application = spy(new RapidFtrApplication(CustomTestRunner.INJECTOR));
    }

	@Test
	public void shouldSaveServerUrlAfterSuccessfulLogin() throws IOException {
		User user = createUser();
		user.setServerUrl("http://test-server-url");
		application.setCurrentUser(user);

		Assert.assertThat(application.getSharedPreferences().getString(SERVER_URL_PREF, ""), equalTo(user.getServerUrl()));
	}

//	@Test
//	public void testInitializeFormSections() throws JSONException, IOException {
//		List<FormSection> formSections = (List<FormSection>) mock(List.class);
//		RapidFtrApplication.getApplicationInstance().setFormSections(formSections);
//
//		activity.initializeData(null);
//		assertThat(activity.formSections, equalTo(formSections));
//	}


//    @Test
//    public void shouldReturnUserName() {
//        application.setPreference(RapidFtrApplication.Preference.USER_NAME, "testUser");
//        assertThat(application.getUserName(), equalTo("testUser"));
//    }
//
//    @Test
//    public void shouldReturnUserObject() throws JSONException {
//        application.setPreference(RapidFtrApplication.Preference.USER_NAME, "testUser");
//        application.setPreference("testUser", "{ 'abc' : 'xyz' }");
//        User user = application.buildUser();
//        assertThat(user.getString("abc"), equalTo("xyz"));
//    }

}
