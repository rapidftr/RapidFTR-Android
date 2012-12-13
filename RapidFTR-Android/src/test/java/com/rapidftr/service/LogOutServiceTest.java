package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class LogOutServiceTest {

    @Test
    public void shouldUpdateContextOnLogout(){
        LogOutService service = new LogOutService();
        RapidFtrApplication context = mock(RapidFtrApplication.class);

        service.logOut(context);

        verify(context).setLoggedIn(false);
        verify(context).removePreference(USER_NAME);
        verify(context).removePreference(USER_ORG);

    }
}
