package com.rapidftr.service;

import android.content.SharedPreferences;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import com.rapidftr.repository.EnquiryRepository;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.SyncFailedException;
import java.util.Arrays;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.LAST_ENQUIRY_SYNC;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EnquirySyncServiceTest {

    @Mock
    private EnquiryHttpDao enquiryHttpDao;
    @Mock
    private EnquiryRepository enquiryRepository;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private User user;

    @Test
    public void getRecordShouldRetrieveARecordOverHttp() throws Exception {
        String enquiryInternalId = "enquiryInternalId";
        EnquirySyncService enquirySyncService = new EnquirySyncService(sharedPreferences, enquiryHttpDao, enquiryRepository);
        Enquiry expectedEnquiry = new Enquiry("createdBy", "reporterName", new JSONObject("{}"));
        when(enquiryHttpDao.get(enquiryInternalId)).thenReturn(expectedEnquiry);

        final Enquiry downloadedEnquiry = enquirySyncService.getRecord(enquiryInternalId);

        assertThat(downloadedEnquiry.getUniqueId(), is(expectedEnquiry.getUniqueId()));
    }

    @Test
    public void getIdsToDownloadShouldRetrieveUrlsFromApiSinceLastUpdate() throws Exception {
        EnquirySyncService enquirySyncService = new EnquirySyncService(sharedPreferences, enquiryHttpDao, enquiryRepository);
        when(enquiryHttpDao.getIdsOfUpdated()).thenReturn(Arrays.asList("blah.com/123", "blah.com/234"));

        List<String> enquiryIds = enquirySyncService.getIdsToDownload();

        assertThat(enquiryIds.get(0), is("blah.com/123"));
        assertThat(enquiryIds.get(1), is("blah.com/234"));
    }

    @Test
    public void shouldUpdateEnquiryWhenItIsNotNew() throws Exception {
        Enquiry enquiry = mock(Enquiry.class);
        Enquiry returnedEnquiry = mock(Enquiry.class);

        when(enquiry.isNew()).thenReturn(false);
        when(enquiryHttpDao.update(enquiry)).thenReturn(returnedEnquiry);

        new EnquirySyncService(sharedPreferences, enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        verify(enquiryRepository).createOrUpdate(returnedEnquiry);
    }

    @Test
    public void shouldCreateEnquiryWhenItIsNew() throws Exception {
        Enquiry enquiry = mock(Enquiry.class);
        Enquiry returnedEnquiry = mock(Enquiry.class);

        when(enquiry.isNew()).thenReturn(true);
        when(enquiryHttpDao.create(enquiry)).thenReturn(returnedEnquiry);

        new EnquirySyncService(sharedPreferences, enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        verify(enquiryRepository).createOrUpdate(returnedEnquiry);
    }

    @Test
    public void shouldUpdateEnquiryAttributesAfterSync() throws Exception {
        Enquiry enquiry = mock(Enquiry.class);
        Enquiry returnedEnquiry = new Enquiry();

        when(enquiry.isNew()).thenReturn(false);
        when(enquiryHttpDao.update(enquiry)).thenReturn(returnedEnquiry);

        assertThat(returnedEnquiry.isSynced(), CoreMatchers.is(false));
        assertNull(returnedEnquiry.getLastUpdatedAt());

        new EnquirySyncService(sharedPreferences, enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        assertNotNull(returnedEnquiry.getLastUpdatedAt());
        assertThat(returnedEnquiry.isSynced(), CoreMatchers.is(true));
    }

    @Test(expected = SyncFailedException.class)
    public void shouldHandleSyncFailuresAndReturnEnquiry() throws Exception {
        Enquiry enquiry = new Enquiry();

        when(enquiryHttpDao.create(enquiry)).thenThrow(SyncFailedException.class);
        new EnquirySyncService(sharedPreferences, enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        assertFalse(enquiry.isSynced());
        assertNull(enquiry.getLastUpdatedAt());
    }
}
