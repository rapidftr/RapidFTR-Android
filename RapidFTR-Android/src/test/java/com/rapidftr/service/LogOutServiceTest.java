package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class LogOutServiceTest {

    @Test
    public void shouldUpdateContextOnLogout() throws IOException {
        LogOutService service = new LogOutService();
        RapidFtrApplication context = spy(RapidFtrApplication.getApplicationInstance());
	    RapidFtrActivity currentActivity = mock(RapidFtrActivity.class);
        given(currentActivity.getContext()).willReturn(context);
        context.setSyncTask(null);
        doReturn("You have been logged out successfully.").when(currentActivity).getString(anyInt());
        service.attemptLogOut(currentActivity);
        verify(context).setCurrentUser(null);
    }
}