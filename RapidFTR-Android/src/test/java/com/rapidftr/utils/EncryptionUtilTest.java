package com.rapidftr.utils;

import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class EncryptionUtilTest {
    @Test
    public void shouldBeAbleToEncryptAndDecryptPassword() throws Exception {
        String passwordHash = EncryptionUtil.encrypt("password", "password");
        String password = EncryptionUtil.decrypt("password", passwordHash);
        assertEquals("password", password);
    }
}
