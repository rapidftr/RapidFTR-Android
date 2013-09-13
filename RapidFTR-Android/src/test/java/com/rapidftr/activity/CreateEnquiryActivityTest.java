package com.rapidftr.activity;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

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
    public void testInitializeEnquiry() throws JSONException {
        activity.initializeData(null);
        assertEquals(activity.enquiry.getClass(), Enquiry.class);
    }

    @Test
    public void testSaveListener() throws JSONException {
        doReturn(new Enquiry()).when(activity).save();
        activity.initializeView();

        activity.findViewById(R.id.submit).performClick();
        verify(activity).save();
    }
}
