package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rapidftr.RapidFtrApplication.Preference.USER_NAME;
import static com.rapidftr.RapidFtrApplication.Preference.USER_ORG;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class LogOutServiceTest {

    @Test
    public void shouldUpdateContextOnLogout(){
        LogOutService service = new LogOutService();
        RapidFtrActivity currentActivity = mock(RapidFtrActivity.class);
        RapidFtrApplication context = mock(RapidFtrApplication.class);
        given(currentActivity.getContext()).willReturn(context);
        RapidFtrApplication.getApplicationInstance().setSyncTask(null);
        doReturn("You have been logged out successfully.").when(currentActivity).getString(anyInt());
        service.attemptLogOut(currentActivity);

        verify(context).setLoggedIn(false);
        verify(context).removePreference(USER_NAME);
        verify(context).removePreference(USER_ORG);
    }
}