package com.rapidftr.service;

import android.content.SharedPreferences;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.LAST_ENQUIRY_SYNC;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnquirySyncServiceTest {

    @Mock
    private EnquiryHttpDao enquiryHttpDao;

    @Mock
    private EnquiryRepository enquiryRepository;

    @Mock
    private SharedPreferences sharedPreferences;


    @Test
    public void getRecord_shouldRetrieveARecordOverHttp() throws Exception {
        String enquiryInternalId = "enquiryInternalId";
        EnquirySyncService enquirySyncService = new EnquirySyncService(sharedPreferences, enquiryHttpDao);
        Enquiry expectedEnquiry = new Enquiry("createdBy", "reporterName", new JSONObject("{}"));
        when(enquiryHttpDao.getEnquiry(enquiryInternalId)).thenReturn(expectedEnquiry);

        final Enquiry downloadedEnquiry = enquirySyncService.getRecord(enquiryInternalId);

        assertThat(downloadedEnquiry.getUniqueId(), is(expectedEnquiry.getUniqueId()));
    }

    @Test
    public void getIdsToDownload_shouldRetrieveUrlsFromApiSinceLastUpdate() throws Exception {
        final long lastUpdateMillis = System.currentTimeMillis();
        EnquirySyncService enquirySyncService = new EnquirySyncService(sharedPreferences, enquiryHttpDao);
        when(sharedPreferences.getLong(LAST_ENQUIRY_SYNC, 0)).thenReturn(lastUpdateMillis);
        when(enquiryHttpDao.getIdsOfUpdated(new DateTime(lastUpdateMillis))).thenReturn(Arrays.asList("blah.com/123", "blah.com/234"));

        List<String> enquiryIds = enquirySyncService.getIdsToDownload();

        assertThat(enquiryIds.get(0), is("blah.com/123"));
        assertThat(enquiryIds.get(1), is("blah.com/234"));
    }


}