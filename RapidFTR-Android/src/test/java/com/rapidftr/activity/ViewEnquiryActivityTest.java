package com.rapidftr.activity;

import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

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

}
