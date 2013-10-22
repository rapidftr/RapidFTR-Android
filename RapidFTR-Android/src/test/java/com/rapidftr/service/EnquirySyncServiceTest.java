package com.rapidftr.service;

import android.content.SharedPreferences;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import com.rapidftr.repository.EnquiryRepository;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.LAST_ENQUIRY_SYNC;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnquirySyncServiceTest {

    @Mock
    private EnquiryHttpDao enquiryHttpDao;

    @Mock
    private EnquiryRepository enquiryRepository;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock private User currentUser;


    @Test
    public void getRecordShouldRetrieveARecordOverHttp() throws Exception {
        String enquiryInternalId = "enquiryInternalId";
        EnquirySyncService enquirySyncService = new EnquirySyncService(sharedPreferences, enquiryHttpDao);
        Enquiry expectedEnquiry = new Enquiry("createdBy", "reporterName", new JSONObject("{}"));
        when(enquiryHttpDao.get(enquiryInternalId)).thenReturn(expectedEnquiry);

        final Enquiry downloadedEnquiry = enquirySyncService.getRecord(enquiryInternalId);

        assertThat(downloadedEnquiry.getUniqueId(), is(expectedEnquiry.getUniqueId()));
    }

    @Test
    public void getIdsToDownloadShouldRetrieveUrlsFromApiSinceLastUpdate() throws Exception {
        final long lastUpdateMillis = System.currentTimeMillis();
        EnquirySyncService enquirySyncService = new EnquirySyncService(sharedPreferences, enquiryHttpDao);
        when(sharedPreferences.getLong(LAST_ENQUIRY_SYNC, 0)).thenReturn(lastUpdateMillis);
        when(enquiryHttpDao.getIdsOfUpdated(new DateTime(lastUpdateMillis))).thenReturn(Arrays.asList("blah.com/123", "blah.com/234"));

        List<String> enquiryIds = enquirySyncService.getIdsToDownload();

        assertThat(enquiryIds.get(0), is("blah.com/123"));
        assertThat(enquiryIds.get(1), is("blah.com/234"));
    }

    @Test
    @Ignore
    public void shouldCreateEnquiryWhenDoesNotExists() throws Exception {
        Enquiry enquiry = new Enquiry("createdBy", "reporterName", new JSONObject("{}"));
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/enquiries", "{ 'test1' : 'value2', '_id' : 'abcd1234'}");

        enquiry = new EnquirySyncService(sharedPreferences, enquiryHttpDao).sync(enquiry, currentUser);

        verify(enquiryRepository).update(enquiry);
    }

}
