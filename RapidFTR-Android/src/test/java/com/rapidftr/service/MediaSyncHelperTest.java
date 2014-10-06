package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.http.FluentRequest;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class MediaSyncHelperTest {

    @Mock
    private User currentUser;
    private EntityHttpDao<Child> childHttpDao;
    private RapidFtrApplication context;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        childHttpDao = EntityHttpDaoFactory.createChildHttpDao(
                "http://whatever",
                ChildSyncService.CHILDREN_API_PATH,
                ChildSyncService.CHILDREN_API_PARAMETER);
        given(currentUser.isVerified()).willReturn(true);
        context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
    }

    @Test
    public void shouldFetchAudioFromServer() throws JSONException, IOException, GeneralSecurityException {
        Child child = new Child("id1", "user1", "{ '_id' : '1234abcd' ,'recorded_audio' : 'audio_file_name'}");

        getFakeHttpLayer().setDefaultHttpResponse(200, "audio stream");
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children/1234abcd/audio", "OK");

        String response = CharStreams.toString(new InputStreamReader(new MediaSyncHelper(childHttpDao, context).getAudio(child)));
        assertEquals("OK", response);
    }
}
