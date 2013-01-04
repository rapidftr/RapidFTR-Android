package com.rapidftr.utils;

import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class EncryptionUtilTest {
    @Test
    public void testEncryptAndDecryptOfPasswordResultsInSameHash() throws Exception {
     String passwordHash = EncryptionUtil.encryptWithoutASeed("password");
     String password = EncryptionUtil.decryptWithoutASeed(passwordHash);
     assertEquals("password", password);
    }
}
