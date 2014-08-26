package com.rapidftr.activity;

import android.widget.ListView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.service.FormService;
import com.rapidftr.utils.SpyActivityController;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewAllEnquiryActivityTest {
    private ActivityController<ViewAllEnquiryActivity> activityController;
    protected ViewAllEnquiryActivity activity;

    @Mock
    private EnquiryRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        activityController = SpyActivityController.of(ViewAllEnquiryActivity.class);
        activity = activityController.attach().get();

        RapidFtrApplication application = RapidFtrApplication.getApplicationInstance();

        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(new FormService(application)).when(mockInjector).getInstance(FormService.class);
        doReturn(repository).when(mockInjector).getInstance(EnquiryRepository.class);
    }

    @Test
    public void shouldListAllEnquiries() throws JSONException {
        List<Enquiry> enquiries = new ArrayList<Enquiry>();
        enquiries.add(new Enquiry("{name:NAME}", "CREATEDBY"));
        enquiries.add(new Enquiry("{name:NAME}", "CREATEDBY"));
        when(repository.allCreatedByCurrentUser()).thenReturn(enquiries);

        activityController.create();
        ListView listView = (ListView) activity.findViewById(R.id.enquiry_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
        assertEquals(listView.getCount(), 2);
    }

    @Test
    public void shoudShowNoEnquiriesMessageWhenNoEnquiriesPresent() throws JSONException {
        List<Enquiry> enquiries = new ArrayList<Enquiry>();
        when(repository.all()).thenReturn(enquiries);
        activityController.create();
        ListView listView = (ListView) activity.findViewById(R.id.enquiry_list);
        assertNotNull(listView.getEmptyView());
        assertEquals(listView.getCount(), 0);
    }
}
