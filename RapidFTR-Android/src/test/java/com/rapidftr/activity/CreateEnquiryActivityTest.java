package com.rapidftr.activity;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class CreateEnquiryActivityTest {
    private CreateEnquiryActivity activity;

    @Before
    public void setUp(){
        activity = spy(new CreateEnquiryActivity());
    }

    @Test
    public void testRenderLayout(){
        activity.onCreate(null);
        verify(activity).setContentView(R.layout.activity_create_enquiry);
    }

    @Test
    public void testInitializeEnquiry() throws Exception {
        activity.initializeData(null);
        assertEquals(activity.enquiry.getClass(), Enquiry.class);
    }

}
