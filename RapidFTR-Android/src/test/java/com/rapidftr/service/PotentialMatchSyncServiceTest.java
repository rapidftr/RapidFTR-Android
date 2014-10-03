package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.PotentialMatch;
import com.rapidftr.model.User;
import com.rapidftr.repository.PotentialMatchRepository;
import com.rapidftr.utils.http.FluentRequest;
import org.apache.http.HttpException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.rapidftr.RapidFtrApplication.getApplicationInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class PotentialMatchSyncServiceTest {

    @Mock
    private User currentUser;
    @Mock
    private PotentialMatchRepository repository;
    FluentRequest fluentRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        given(currentUser.isVerified()).willReturn(true);
        fluentRequest = new FluentRequest();
    }

    @Test
    public void shouldFetchListOfResourceUrlsToUpdate() throws Exception {
        String response = "[{\"location\":\"http://testserver/api/potential_matches/cc6d605e5f5591551a62f9cd181ee832\"}]";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/potential_matches?updated_after=1970-01-01%2000%3A00%3A00UTC", response);

        List<String> resourceUrlsToUpdate = new PotentialMatchSyncService(mockContext(), repository).getIdsToDownload();
        assertEquals(1, resourceUrlsToUpdate.size());
        assertEquals("http://testserver/api/potential_matches/cc6d605e5f5591551a62f9cd181ee832", resourceUrlsToUpdate.get(0));
    }

    @Test
    public void shouldUseLastPotentialMatchSyncTimestampToRetreiveIds() throws Exception {
        String response = "[{\"location\":\"http://whatever/api/children/5-1ed26a0e5072830a9064361a570684f6\"},{\"location\":\"http://whatever/api/children/4-b011946150a16b0d2c6271aed05e2abe\"}]";
        long time = 1412330399491l;
        getApplicationInstance().getSharedPreferences().edit().putLong(RapidFtrApplication.LAST_POTENTIAL_MATCH_SYNC, time).commit();

        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/potential_matches?updated_after=2014-10-03%2009%3A59%3A59UTC", response);
        new PotentialMatchSyncService(mockContext(), repository).getIdsToDownload();
    }

    @Test
    public void shouldGetPotentialMatch() throws IOException, JSONException, HttpException {
        String resourceUrl = "http://whatever/api/potential_matches/dfb2031ebfb468f5200edc";
        String response = "{\"_id\" : \"couch_id\", \"child_id\":\"a0b2135fff78223s4h1edc\",\"enquiry_id\":\"78223s4h1e468f5200edc\"}";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/potential_matches/dfb2031ebfb468f5200edc/", response);
        PotentialMatchSyncService service = new PotentialMatchSyncService(mockContext(), repository);
        PotentialMatch record = service.getRecord(resourceUrl);

        assertEquals("a0b2135fff78223s4h1edc", record.getChildId());
        assertEquals("78223s4h1e468f5200edc", record.getEnquiryId());
        assertEquals("couch_id", record.getUniqueId());
    }

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }
}
