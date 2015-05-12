package com.rapidftr.service;

import android.content.SharedPreferences;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import com.rapidftr.repository.EnquiryRepository;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import javax.xml.ws.http.HTTPException;
import java.io.SyncFailedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.rapidftr.RapidFtrApplication.getApplicationInstance;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class EnquirySyncServiceTest {

    private EntityHttpDao<Enquiry> enquiryHttpDao;
    @Mock
    private EnquiryRepository enquiryRepository;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private User user;

    @Before
    public void setUp() {
        initMocks(this);

        enquiryHttpDao = EntityHttpDaoFactory.createEnquiryHttpDao(
                "http://whatever",
                EnquiryHttpDao.ENQUIRIES_API_PATH,
                EnquirySyncService.ENQUIRIES_API_PARAMETER);
        given(user.isVerified()).willReturn(true);
    }

    @Test
    public void getRecordShouldRetrieveARecordOverHttp() throws Exception {
        String resourceUrl = "http://whatever/api/enquiries/dfb2031ebfb468f5200edc";
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/enquiries/dfb2031ebfb468f5200edc/", response);
        EnquirySyncService enquirySyncService = new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository);

        Enquiry expectedEnquiry = new Enquiry(response, "createdBy");

        final Enquiry downloadedEnquiry = enquirySyncService.getRecord(resourceUrl);
        assertThat(downloadedEnquiry.getUniqueId(), is(expectedEnquiry.getUniqueId()));
    }

    @Test
    public void getIdsToDownloadShouldRetrieveUrlsFromApiSinceLastUpdate() throws Exception {
        String response = "[{\"location\":\"http://blah.com/123\"},{\"location\":\"http://blah.com/234\"}]";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/enquiries?updated_after=1970-01-01%2000%3A00%3A00UTC", response);

        List<String> enquiryIds = new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).getIdsToDownload();

        assertThat(enquiryIds.get(0), is("http://blah.com/123"));
        assertThat(enquiryIds.get(1), is("http://blah.com/234"));
    }

    @Test
    public void shouldUseLastEnquirySyncTimestampToRetreiveIds() throws Exception {
        String response = "[{\"location\":\"http://whatever/api/children/5-1ed26a0e5072830a9064361a570684f6\"},{\"location\":\"http://whatever/api/children/4-b011946150a16b0d2c6271aed05e2abe\"}]";
        long time = 1412330399491l;
        getApplicationInstance().getSharedPreferences().edit().putLong(RapidFtrApplication.LAST_ENQUIRY_SYNC, time).commit();

        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/enquiries?updated_after=2014-10-03%2009%3A59%3A59UTC", response);
        new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).getIdsToDownload();
    }

    @Test
    public void shouldNotSendUnnecessaryParamsDuringSync() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"photo_keys\":[], \"audio_attachments\":\"[]\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry record = new Enquiry(response);
        Enquiry recordSpy = spy(record);
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        EnquirySyncService enquirySyncService = new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository);
        enquirySyncService.sync(recordSpy, user);

        verify(recordSpy).remove("photo_keys");
        verify(recordSpy).remove("audio_attachments");
        verify(recordSpy).remove("synced");
    }

    @Test
    public void shouldUpdateEnquiryWhenItIsNotNew() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry enquiry = spy(new Enquiry(response, "createdBy"));
        enquiry.put(Enquiry.FIELD_INTERNAL_ID, "id");

        doReturn(false).when(enquiry).isNew();

        BasicHttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        httpResponse.setEntity(new StringEntity(response, ContentType.APPLICATION_JSON));

        getFakeHttpLayer().addHttpResponseRule("PUT", "http://whatever/api/enquiries/id", httpResponse);
        Enquiry returnedEnquiry = new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        verify(enquiryRepository).createOrUpdateWithoutHistory(returnedEnquiry);
    }

    @Test
    public void shouldRemoveHistoriesAfterSuccessfulSync() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry enquirySpy = spy(new Enquiry(response, "createdBy"));
        enquirySpy.put(Enquiry.FIELD_INTERNAL_ID, "id");
        doReturn(false).when(enquirySpy).isNew();

        enquiryHttpDao = mock(EntityHttpDao.class);
        doReturn(enquirySpy).when(enquiryHttpDao).update(any(Enquiry.class), any(String.class), any(Map.class));
        new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).sync(enquirySpy, user);

        verify(enquirySpy).remove(History.HISTORIES);
    }

    @Test(expected = SyncFailedException.class)
    public void shouldNotRemoveHistoriesAfterFailedSync() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry enquirySpy = spy(new Enquiry(response, "createdBy"));
        doReturn(false).when(enquirySpy).isNew();

        enquiryHttpDao = mock(EntityHttpDao.class);
        doThrow(new HTTPException(404)).when(enquiryHttpDao).update(any(Enquiry.class), any(String.class), any(Map.class));
        new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).sync(enquirySpy, user);

        verify(enquirySpy, never()).remove(History.HISTORIES);
    }

    @Test
    public void shouldCreateEnquiryWhenItIsNew() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry enquiry = spy(new Enquiry(response, "createdBy"));

        doReturn(true).when(enquiry).isNew();

        BasicHttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        httpResponse.setEntity(new StringEntity(response, ContentType.APPLICATION_JSON));

        getFakeHttpLayer().addHttpResponseRule("POST", "http://whatever/api/enquiries", httpResponse);
        Enquiry returnedEnquiry = new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        verify(enquiryRepository).createOrUpdateWithoutHistory(returnedEnquiry);
    }

    @Test
    public void shouldUpdateEnquiryAttributesAfterSync() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry enquiry = spy(new Enquiry(response, "createdBy"));
        enquiry.put(Enquiry.FIELD_INTERNAL_ID, "id");

        doReturn(false).when(enquiry).isNew();

        BasicHttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        httpResponse.setEntity(new StringEntity(response, ContentType.APPLICATION_JSON));

        getFakeHttpLayer().addHttpResponseRule("PUT", "http://whatever/api/enquiries/id", httpResponse);
        Enquiry returnedEnquiry = new Enquiry();

        assertThat(returnedEnquiry.isSynced(), CoreMatchers.is(false));
        assertNull(returnedEnquiry.getLastUpdatedAt());

        returnedEnquiry = new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        verify(enquiryRepository).createOrUpdateWithoutHistory(returnedEnquiry);
        assertNotNull(returnedEnquiry.getLastUpdatedAt());
        assertThat(returnedEnquiry.isSynced(), CoreMatchers.is(true));
    }

    @Test(expected = SyncFailedException.class)
    public void shouldHandleSyncFailuresAndReturnEnquiry() throws Exception {
        String response = "{\"_id\" : \"couch_id\", \"child_name\":\"subhas\",\"unique_identifier\":\"78223s4h1e468f5200edc\"}";
        Enquiry enquiry = spy(new Enquiry(response, "createdBy"));

        doReturn(true).when(enquiry).isNew();

        BasicHttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "ERROR");
        httpResponse.setEntity(new StringEntity(response, ContentType.APPLICATION_JSON));

        getFakeHttpLayer().addHttpResponseRule("POST", "http://whatever/api/enquiries/", httpResponse);

        new EnquirySyncService(mockContext(), enquiryHttpDao, enquiryRepository).sync(enquiry, user);

        assertFalse(enquiry.isSynced());
        assertNull(enquiry.getLastUpdatedAt());
    }

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }
}
