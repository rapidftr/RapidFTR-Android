package com.rapidftr;

import com.rapidftr.model.User;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class RapidFtrApplicationTest {

    private RapidFtrApplication application;

    @Before
    public void setUp() {
        application = spy(new RapidFtrApplication(CustomTestRunner.INJECTOR));
    }

    @Test
    public void shouldReturnUserName() {
        application.setPreference(RapidFtrApplication.Preference.USER_NAME, "testUser");
        assertThat(application.getUserName(), equalTo("testUser"));
    }

    @Test
    public void shouldReturnUserObject() throws JSONException {
        application.setPreference(RapidFtrApplication.Preference.USER_NAME, "testUser");
        application.setPreference("testUser", "{ 'abc' : 'xyz' }");
        User user = application.getUser();
        assertThat(user.getString("abc"), equalTo("xyz"));
    }

}
