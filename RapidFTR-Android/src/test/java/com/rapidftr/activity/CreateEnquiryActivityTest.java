package com.rapidftr.activity;

import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.service.FormService;
import com.rapidftr.utils.SpyActivityController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class CreateEnquiryActivityTest {
    private CreateEnquiryActivity activity;
    private ActivityController<CreateEnquiryActivity> activityController;

    @Before
    public void setUp() {
        activityController = SpyActivityController.of(CreateEnquiryActivity.class);
        activity = activityController.attach().get();

        RapidFtrApplication application = RapidFtrApplication.getApplicationInstance();
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(new FormService(application)).when(mockInjector).getInstance(FormService.class);
    }

    @Test
    public void testRenderLayout() {
        activityController.create();
        verify(activity).setContentView(R.layout.activity_create_enquiry);
    }

    @Test
    public void testInitializeEnquiry() throws Exception {
        activity.initializeData(null);
        assertEquals(activity.enquiry.getClass(), Enquiry.class);
    }

}
