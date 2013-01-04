package com.rapidftr.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EncryptionUtil {
    public static String encrypt(String seed, String textToEncrypt) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.encodeToString(cipher.doFinal(textToEncrypt.getBytes()), Base64.DEFAULT);
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT)));
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        kgen.init(128, secureRandom); // 192 and 256 bits may not be available
        return kgen.generateKey().getEncoded();
    }
    
    public static String  encryptWithoutASeed(String textToEncrypt) throws Exception {
        return Base64.encodeToString(textToEncrypt.getBytes("UTF-8"), Base64.DEFAULT);
    }

    public static String  decryptWithoutASeed(String textToDecrypt) throws Exception {
        return new String(Base64.decode(textToDecrypt, Base64.DEFAULT), "UTF-8");
    }

}
