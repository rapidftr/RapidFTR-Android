package com.rapidftr.activity;

import android.content.Intent;
import android.widget.ListView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.xtremelabs.robolectric.Robolectric;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewEnquiryActivityTest {
    protected ViewEnquiryActivity activity;
    
    public DatabaseSession session;
    
    @Mock
    private EnquiryRepository enquiryRepository;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private Enquiry enquiry;
    @Before
    public void setUp()throws Exception{
        initMocks(this);
        activity = spy(new ViewEnquiryActivity());
        
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(enquiry).when(mockInjector).getInstance(Enquiry.class);
        doReturn(enquiryRepository).when(mockInjector).getInstance(EnquiryRepository.class);
        doReturn(childRepository).when(mockInjector).getInstance(ChildRepository.class);
        session = new ShadowSQLiteHelper("test_database").getSession();
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorIfChildIsNotFound() throws Exception{
        activity.initializeData(null);
    }
    
    @Test
    public void shouldListMatchedChildren() throws JSONException {
        Child child = new Child("id1", "user1", "{ \"name\" : \"child1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }");
        ArrayList<String> listOfIds = new ArrayList<String>();
        ArrayList<Child> listOfChildren = new ArrayList<Child>();
        listOfChildren.add(child);
        listOfIds.add("id1");

        enquiryRepository.createOrUpdate(enquiry);
        when(enquiry.getUniqueId()).thenReturn("uniqueId");
        when(enquiryRepository.get(enquiry.getUniqueId())).thenReturn(enquiry);
        when(enquiry.getPotentialMatches()).thenReturn(listOfIds);
        when(childRepository.getChildrenByIds(listOfIds)).thenReturn(listOfChildren);
        Robolectric.shadowOf(activity).setIntent(new Intent().putExtra("id", enquiry.getUniqueId().toString()));
        activity.onCreate(null);
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
    }

}
