package com.rapidftr.model;

import android.content.SharedPreferences;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(CustomTestRunner.class)
public class UserTest {

	@Test
	public void testOne() {
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
