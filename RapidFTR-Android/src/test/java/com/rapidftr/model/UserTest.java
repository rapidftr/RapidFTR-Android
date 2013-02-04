package com.rapidftr.model;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.rapidftr.CustomTestRunner.createUser;
import static com.rapidftr.model.User.UNAUTHENTICATED_DB_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class UserTest {

	User user;

	@Before
	public void setUp() throws IOException {
		user = spy(createUser());
		doReturn("{}").when(user).asJSON();
	}

	@Test
	public void shouldSavePlainTextPasswordForUnverified() throws IOException, GeneralSecurityException {
		user.setAuthenticated(false);
		user.save();
		verify(user).setUnauthenticatedPassword(user.getPassword());
	}

	@Test
	public void shouldSetUnauthenticatedDbKeyForUnverifiedUsers() throws IOException, GeneralSecurityException {
		user.setAuthenticated(false);
		user.save();
		verify(user).setDbKey(User.getUnauthenticatedDbKey());
	}

	@Test
	public void shouldReturnUnauthenticatedDbKeyWhenPresent() {
		RapidFtrApplication.getApplicationInstance().getSharedPreferences().edit().putString(UNAUTHENTICATED_DB_KEY, "abcd").commit();
		assertThat(User.getUnauthenticatedDbKey(), equalTo("abcd"));
	}

	@Test
	public void shouldCreateAndStoreUnauthenticatedDbKeyWhenNotPresent() {
		RapidFtrApplication.getApplicationInstance().getSharedPreferences().edit().remove(UNAUTHENTICATED_DB_KEY).commit();
		String dbKey = User.createUnauthenticatedDbKey();
		assertThat(User.getUnauthenticatedDbKey(), equalTo(dbKey));
	}

	@Test
	public void shouldSaveUnauthenticatedDbKeyForUnauthenticatedUsers() throws IOException, GeneralSecurityException {
		User user = spy(createUser());
		user.setAuthenticated(false);
		user.setUnauthenticatedPassword(null);

		doReturn("{}").when(user).asJSON();
		user.save();
		assertThat(user.getUnauthenticatedPassword(), equalTo(user.getPassword()));
	}

	@Test
	public void shouldReadDataFromServerResponse() throws IOException {
		User user = new User("testUser", "testPassword");
		String json = "{  \"session\":  {  \"link\":  {  \"rel\":  \"session\",  \"uri\":  \"/sessions/83407cc8a01fb202f25458476e5cb36d\"  },  \"token\":  \"83407cc8a01fb202f25458476e5cb36d\"  },  \"db_key\":  \"6127d30bea89f2fb\",  \"organisation\":  \"N/A\"  }";
		user.read(json);
		assertThat(user.getDbKey(), equalTo("6127d30bea89f2fb"));
		assertThat(user.getOrganisation(), equalTo("N/A"));
	}

	@Test
	public void shouldSaveEncryptedUserDataToSharedPreference() {
	}

	@Test
	public void shouldLoadUserDataFromSharedPreference() {
	}

//    @Test
//    public void shouldSetUnauthenticatedDBKeyWithUUID() throws Exception {
//        User user = new User(false, "organisation", "full name", "password");
//        assertNotNull(user.getDbKey());
//        assertEquals(36, user.getDbKey().length());
//    }
//
//    @Test
//    public void shouldSetDbKeyForAuthenticatedUsers() throws Exception {
//        User user = new User(true, "dbkey", "organisation");
//        assertNotNull(user.getDbKey());
//        assertEquals("dbkey", user.getDbKey());
//    }
//
//    @Test(expected = RuntimeException.class)
//    public void shouldRaiseRuntimeExceptionForInvalidUsers() throws JSONException {
//        new User(null);
//    }
//
//    @Test
//    public void shouldGetSameDbKeyForAllUnauthorizedUsers() throws Exception {
//        User user1 = new User(false, "organisation", "user1", "password");
//        User user2 = new User(false, "organisation", "user2", "password");
//        SharedPreferences sharedPreferences = RapidFtrApplication.getApplicationInstance().getSharedPreferences();
//        sharedPreferences.edit().putString("user1", user1.toString()).commit();
//        sharedPreferences.edit().putString("user2", user2.toString()).commit();
//        assertEquals(new User(sharedPreferences.getString("user1", "")).getDbKey(), new User(sharedPreferences.getString("user2", "")).getDbKey());
//    }
//
//    @Test
//    public void shouldGetDifferentDbKeysForAuthorizedAndUnAuthorizedUsers() throws Exception {
//        User user1 = new User(false, "organisation", "user1", "password");
//        User user2 = new User(false, "organisation", "user2", "password");
//        User user3 = new User(true, "dbkey", "organisation");
//        User user4 = new User(true, "dbkey", "organisation");
//        SharedPreferences sharedPreferences = RapidFtrApplication.getApplicationInstance().getSharedPreferences();
//        sharedPreferences.edit().putString("user1", user1.toString()).commit();
//        sharedPreferences.edit().putString("user2", user2.toString()).commit();
//        sharedPreferences.edit().putString("user3", user3.toString()).commit();
//        sharedPreferences.edit().putString("user4", user4.toString()).commit();
//        assertEquals(new User(sharedPreferences.getString("user1", "")).getDbKey(), new User(sharedPreferences.getString("user2", "")).getDbKey());
//        assertEquals(new User(sharedPreferences.getString("user3", "")).getDbKey(), new User(sharedPreferences.getString("user4", "")).getDbKey());
//        assertFalse(new User(sharedPreferences.getString("user1", "")).getDbKey().equals(new User(sharedPreferences.getString("user4", "")).getDbKey()));
//    }
    
}
