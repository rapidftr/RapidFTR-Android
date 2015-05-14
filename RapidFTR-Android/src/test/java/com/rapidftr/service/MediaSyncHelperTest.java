package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.User;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class MediaSyncHelperTest {

    @Mock
    private User currentUser;
    private EntityHttpDao<Child> childHttpDao;
    private RapidFtrApplication application;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        application = (RapidFtrApplication) Robolectric.getShadowApplication().getApplicationContext();
        User user = new User("userName", "password", true, "whatever");
        application.setCurrentUser(user);

        childHttpDao = EntityHttpDaoFactory.createChildHttpDao(application,
                "http://whatever",
                ChildSyncService.CHILDREN_API_PATH,
                ChildSyncService.CHILDREN_API_PARAMETER);
        given(currentUser.isVerified()).willReturn(true);
    }

    @Test
    public void shouldFetchAudioFromServer() throws JSONException, IOException, GeneralSecurityException {
        Child child = new Child("id1", "user1", "{ '_id' : '1234abcd' ,'recorded_audio' : 'audio_file_name'}");

        getFakeHttpLayer().setDefaultHttpResponse(200, "audio stream");
        getFakeHttpLayer().addHttpResponseRule("http://whatever/child/1234abcd/audio", "OK");

        String response = CharStreams.toString(new InputStreamReader(new MediaSyncHelper(childHttpDao, application).getAudio(child)));
        assertEquals("OK", response);
    }

    @Test
    public void shouldBuildPhotoUrlFromChildModel() throws IOException {
        EntityHttpDao<Child> spyDao = spy(new EntityHttpDao<Child>(application));
        MediaSyncHelper helper = new MediaSyncHelper(spyDao, application);
        Child child = new Child();
        child.put("_id", "1234");
        doReturn(null).when(spyDao).getResourceStream(any(String.class));
        helper.getReSizedPhoto(child, "image");
        verify(spyDao).getResourceStream("/child/1234/photo/image/resized/475x635");
    }

    @Test
    public void shouldBuildPhotoUrlFromEnquiryModel() throws IOException, JSONException {
        EntityHttpDao<Child> spyDao = spy(new EntityHttpDao<Child>(application));
        MediaSyncHelper helper = new MediaSyncHelper(spyDao, application);
        Enquiry enquiry = new Enquiry("{}");
        enquiry.put("_id", "1234");
        doReturn(null).when(spyDao).getResourceStream(any(String.class));
        helper.getReSizedPhoto(enquiry, "image");
        verify(spyDao).getResourceStream("/enquiry/1234/photo/image/resized/475x635");
    }

    @Test
    public void shouldBuildAudioUrlFromChildModel() throws IOException {
        EntityHttpDao<Child> spyDao = spy(new EntityHttpDao<Child>(application));
        MediaSyncHelper helper = new MediaSyncHelper(spyDao, application);
        Child child = new Child();
        child.put("_id", "1234");
        doReturn(null).when(spyDao).getResourceStream(any(String.class));
        helper.getAudio(child);
        verify(spyDao).getResourceStream("/child/1234/audio");
    }

    @Test
    public void shouldBuildAudioUrlFromEnquiryModel() throws IOException, JSONException {
        EntityHttpDao<Child> spyDao = spy(new EntityHttpDao<Child>(application));
        MediaSyncHelper helper = new MediaSyncHelper(spyDao, application);
        Enquiry enquiry = new Enquiry("{}");
        enquiry.put("_id", "1234");
        doReturn(null).when(spyDao).getResourceStream(any(String.class));
        helper.getAudio(enquiry);
        verify(spyDao).getResourceStream("/enquiry/1234/audio");
    }
}
