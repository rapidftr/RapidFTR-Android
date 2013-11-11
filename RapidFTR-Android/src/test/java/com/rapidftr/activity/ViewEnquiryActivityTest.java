package com.rapidftr.activity;

import android.os.Bundle;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.FailedToSaveException;
import com.rapidftr.task.SyncRecordTask;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewEnquiryActivityTest {
    protected ViewEnquiryActivity activity;
    
    public DatabaseSession session;
    private EnquiryRepository enquiryRepository;

    @Mock
    private EnquiryRepository mockEnquiryRepository;
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
        doReturn(mockEnquiryRepository).when(mockInjector).getInstance(EnquiryRepository.class);
        doReturn(childRepository).when(mockInjector).getInstance(ChildRepository.class);
        session = new ShadowSQLiteHelper("test_database").getSession();
        enquiryRepository = new EnquiryRepository("user", session);
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorIfChildIsNotFound() throws Exception{
        activity.initializeData(null);
    }

    @Test
    public void shouldInvokeSyncTask() {
        doReturn(mockEnquiryRepository).when(activity).inject(EnquiryRepository.class);
        SyncRecordTask task = mock(SyncRecordTask.class);
        doReturn(task).when(activity).createSyncTaskForEnquiry();
        activity.enquiry = enquiry;
        activity.sync();
        verify(task).setActivity(activity);
        verify(task).doInBackground(enquiry);
    }

    @Test
    public void loadShouldMergeCriteriaWithOtherEnquiryKeys() throws JSONException, FailedToSaveException {
        String enquiryJSON = "{\"enquirer_name\":\"godwin\", " +
                "\"name\":\"robin\", " +
                "\"age\":\"10\", " +
                "\"created_by\":\"John Doe\"," +
                "\"synced\":false}";

        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepository.createOrUpdate(enquiry);

        Bundle bundle = mock(android.os.Bundle.class);
        when(bundle.getString("id")).thenReturn(enquiry.getUniqueId());

        ViewEnquiryActivity viewEnquiryActivity = new ViewEnquiryActivity();
        Enquiry retrievedEnquiry = viewEnquiryActivity.loadEnquiry(bundle, enquiryRepository);
        retrievedEnquiry.remove("id"); //because id attribute is added to the enquiry while saving to the database

        JSONAssert.assertEquals(retrievedEnquiry, enquiry, true);
    }

}
