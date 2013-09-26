package com.rapidftr.activity;

import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewEnquiryActivityTest {
    protected ViewEnquiryActivity activity;
    private EnquiryRepository repository;

    @Before
    public void setUp(){
        initMocks(this);
        activity = spy(new ViewEnquiryActivity());
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(repository).when(mockInjector).getInstance(EnquiryRepository.class);
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorIfChildIsNotFound() throws Exception{
        activity.initializeData(null);
    }

}
