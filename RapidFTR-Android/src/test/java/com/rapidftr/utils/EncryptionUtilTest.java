package com.rapidftr.utils;

import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.CipherOutputStream;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class EncryptionUtilTest {
    @Test
    public void shouldBeAbleToEncryptAndDecryptPassword() throws Exception {
        String passwordHash = EncryptionUtil.encrypt("password", "username", "password");
        String password = EncryptionUtil.decrypt("password", "username", passwordHash);
        assertEquals("password", password);
    }

    @Test
    public void shouldBeAbleToEncryptAndDecryptFile() throws Exception {
        String expected = "test data", password = "test pass word";
        File file = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
        file.deleteOnExit();

        OutputStream outputStream = EncryptionUtil.getCipherOutputStream(file, password);
        outputStream.write(expected.getBytes());
        outputStream.close();

        InputStream inputStream = EncryptionUtil.getCipherInputStream(file, password);
        String actual = new String(IOUtils.toByteArray(inputStream));

        assertEquals(expected, actual);
    }
}
