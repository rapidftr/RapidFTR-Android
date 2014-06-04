package com.rapidftr.activity;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import com.rapidftr.utils.SpyActivityController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class CreateEnquiryActivityTest {
    private CreateEnquiryActivity activity;
    private ActivityController<CreateEnquiryActivity> activityController;

    @Before
    public void setUp(){
        activityController = SpyActivityController.of(CreateEnquiryActivity.class);
        activity = activityController.attach().get();
    }

    @Test
    public void testRenderLayout(){
        activityController.create();
        verify(activity).setContentView(R.layout.activity_create_enquiry);
    }

    @Test
    public void testInitializeEnquiry() throws Exception {
        activity.initializeData(null);
        assertEquals(activity.enquiry.getClass(), Enquiry.class);
    }

}
