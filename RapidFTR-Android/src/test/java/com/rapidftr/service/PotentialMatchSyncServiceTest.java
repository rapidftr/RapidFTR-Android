package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.PotentialMatch;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import com.rapidftr.utils.http.FluentRequest;
import org.apache.http.HttpException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class PotentialMatchSyncServiceTest {

    @Mock private User currentUser;
    @Mock private PotentialMatchRepository repository;
    FluentRequest fluentRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        given(currentUser.isVerified()).willReturn(true);
        fluentRequest = new FluentRequest();
    }

    @Test
    public void shouldMarkPotentialMatchAsSyncedWhenSyncing() throws IOException, JSONException, GeneralSecurityException {

    }

    @Test
    public void shouldUpdatePotentialMatchAttributesAfterSync() throws IOException, JSONException {
    }

    @Test
    public void shouldFetchListOfIdsToUpdate() throws Exception {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        String response = "[{\"_rev\":\"5-1ed26a0a9064361a570684f6\",\"_id\":\"dfb2031ebfb468f5200edc\"}]";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/potential_matches/ids", response);

        List<String> idsToChange = new PotentialMatchSyncService(mockContext(), repository, mockFluentRequest).getIdsToDownload();
        assertEquals(1, idsToChange.size());
        assertEquals("dfb2031ebfb468f5200edc", idsToChange.get(0));
    }

    @Test
    public void shouldNotIncludePreviouslyDownloadedIdsInIdsToUpdate() throws Exception {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        String response = "[{\"_rev\":\"5-1ed26a0a9064361a570684f6\",\"_id\":\"dfb2031ebfb468f5200edc\"}" +
                          ",{\"_rev\":\"5-aaa0a90640f8ahf8aahf868h\",\"_id\":\"a0b2135fff78223s4h1edc\"}]";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/potential_matches/ids", response);

        HashMap<String, String> repositoryIdsAndRevs = new HashMap<String, String>();
        repositoryIdsAndRevs.put("dfb2031ebfb468f5200edc", "5-1ed26a0a9064361a570684f6");
        when(repository.getAllIdsAndRevs()).thenReturn(repositoryIdsAndRevs);

        List<String> idsToChange = new PotentialMatchSyncService(mockContext(), repository, mockFluentRequest).getIdsToDownload();
        assertEquals(1, idsToChange.size());
        assertEquals("a0b2135fff78223s4h1edc", idsToChange.get(0));
    }

    @Test
    public void shouldGetPotentialMatch() throws IOException, JSONException, HttpException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        String id = "dfb2031ebfb468f5200edc";
        String response = "{\"child_id\":\"a0b2135fff78223s4h1edc\",\"enquiry_id\":\"78223s4h1e468f5200edc\"}";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/potential_matches/dfb2031ebfb468f5200edc", response);
        PotentialMatchSyncService service = new PotentialMatchSyncService(mockContext(), repository, mockFluentRequest);
        PotentialMatch record = (PotentialMatch) service.getRecord(id);

        assertEquals("a0b2135fff78223s4h1edc", record.getChildId());
        assertEquals("78223s4h1e468f5200edc", record.getEnquiryId());
    }


    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }
}
