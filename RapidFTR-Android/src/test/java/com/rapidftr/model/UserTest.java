package com.rapidftr.model;

import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(CustomTestRunner.class)
public class UserTest {
    @Test
    public void shouldSetUnauthenticatedDBKeyWithUUID() throws Exception {
        User user = new User(false, "organisation", "full name", "password");
        assertNotNull(user.getDbKey());
        assertEquals(36, user.getDbKey().length());
    }

    @Test
    public void shouldSetDbKeyForAuthenticatedUsers() throws Exception {
        User user = new User(true, "dbkey", "organisation");
        assertNotNull(user.getDbKey());
        assertEquals("dbkey", user.getDbKey());
    }
}
