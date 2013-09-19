package com.rapidftr.activity;

import android.widget.ListView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewAllEnquiryActivityTest {
    protected ViewAllEnquiryActivity activity;

    @Mock
    private EnquiryRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        activity = spy(new ViewAllEnquiryActivity());
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(repository).when(mockInjector).getInstance(EnquiryRepository.class);
    }

    @Test
    public void shouldListAllEnquiries() throws JSONException {
        List<Enquiry> enquiries = new ArrayList<Enquiry>();
        enquiries.add(new Enquiry("CREATEDBY", "REPORTERNAME", new JSONObject("{name:NAME}")));
        enquiries.add(new Enquiry("CREATEDBY", "REPORTERNAME", new JSONObject("{name:NAME}")));
        when(repository.all()).thenReturn(enquiries);

        activity.onCreate(null);
        ListView listView = (ListView) activity.findViewById(R.id.enquiry_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
        assertEquals(listView.getCount(), 2);
    }

    @Test
    public void shoudShowNoEnquiriesMessageWhenNoEnquiriesPresent() throws JSONException {
        List<Enquiry> enquiries = new ArrayList<Enquiry>();
        when(repository.all()).thenReturn(enquiries);
        activity.onCreate(null);
        ListView listView = (ListView) activity.findViewById(R.id.enquiry_list);
        assertNotNull(listView.getEmptyView());
        assertEquals(listView.getCount(), 0);
    }
}
