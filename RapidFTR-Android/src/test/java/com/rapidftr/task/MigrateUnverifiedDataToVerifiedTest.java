package com.rapidftr.task;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class MigrateUnverifiedDataToVerifiedTest {
    private RapidFtrApplication application;
    private User unverifiedUser;
    private User verifiedUser;

    @Before
    public void setUp() throws IOException {
        application = RapidFtrApplication.getApplicationInstance();
        unverifiedUser = new User("username", "pass", false, null, User.UNAUTHENTICATED_DB_KEY, "tw", "user", "pass", "en");
        verifiedUser = new User("username", "pass", true, null, "new_db_key", "tw", "user", "pass", "en");
        application.setCurrentUser(unverifiedUser);
    }

    @Test
    public void shouldMoveChildRecordsFromUnVerifiedDBToVerified() throws JSONException {
        ChildRepository unverifiedChildRepo = mock(ChildRepository.class);
        ChildRepository verifiedChildRepo = mock(ChildRepository.class);

        List<Child> children = asList(new Child("1", unverifiedUser.getUserName(), "{\"name\":\"some content\"}"),
                                      new Child("2", unverifiedUser.getUserName(), "{\"name\":\"some content\"}"),
                                      new Child("3", unverifiedUser.getUserName(), "{\"name\":\"some content\"}"));

        doReturn(children).when(unverifiedChildRepo).allCreatedByCurrentUser();
        doNothing().when(verifiedChildRepo).createOrUpdate(children.get(0));
        doNothing().when(verifiedChildRepo).createOrUpdate(children.get(1));
        doNothing().when(verifiedChildRepo).createOrUpdate(children.get(2));

        JSONObject mockJSONObject = mock(JSONObject.class);
        doReturn(verifiedUser.getDbKey()).when(mockJSONObject).getString("db_key");
        doReturn(verifiedUser.isVerified()).when(mockJSONObject).optBoolean("verified");
        MigrateUnverifiedDataToVerified task = new MigrateUnverifiedDataToVerified(mockJSONObject, unverifiedUser, application);
        task = spy(task);

        doReturn(verifiedUser).when(task).getUserFromResponse();
        doReturn(verifiedChildRepo).when(task).getChildRepo(verifiedUser);
        doReturn(unverifiedChildRepo).when(task).getChildRepo(unverifiedUser);

        task.doInBackground();

        verify(unverifiedChildRepo).deleteChildrenByOwner();
        verify(verifiedChildRepo).createOrUpdate(children.get(0));
        verify(verifiedChildRepo).createOrUpdate(children.get(1));
        verify(verifiedChildRepo).createOrUpdate(children.get(2));
    }

    @Test
    public void shouldSetTheResponseDataToCurrentUser() throws JSONException {
        JSONObject mockJSONObject = mock(JSONObject.class);
        doReturn("DB_KEY_FROM_SERVER").when(mockJSONObject).getString("db_key");
        doReturn(true).when(mockJSONObject).optBoolean("verified");

        MigrateUnverifiedDataToVerified task = new MigrateUnverifiedDataToVerified(mockJSONObject, unverifiedUser, application);
        task = spy(task);

        doReturn(mock(ChildRepository.class)).when(task).getChildRepo(Matchers.<User>any());

        task.doInBackground();

        assertEquals("DB_KEY_FROM_SERVER", application.getCurrentUser().getDbKey());
        assertEquals(true, application.getCurrentUser().isVerified());
    }

}
