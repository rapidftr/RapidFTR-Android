package com.rapidftr.utils;

import android.os.Build;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class EncryptionUtil {

    public static final int JELLY_BEAN_4_2 = 17;

    public static String encrypt(String seed, String textToEncrypt) throws GeneralSecurityException {
        byte[] rawKey = getRawKey(seed.getBytes());
        SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.encodeToString(cipher.doFinal(textToEncrypt.getBytes()), Base64.DEFAULT);
    }

    public static String decrypt(String seed, String encrypted) throws GeneralSecurityException {
        byte[] rawKey = getRawKey(seed.getBytes());
        SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT)));
    }

    public static SecureRandom getSecureRandom() throws NoSuchProviderException, NoSuchAlgorithmException {
        if (Build.VERSION.SDK_INT >= JELLY_BEAN_4_2) {
            return SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            return SecureRandom.getInstance("SHA1PRNG");
        }
    }

    private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = getSecureRandom();
        secureRandom.setSeed(seed);
        kgen.init(128, secureRandom); // 192 and 256 bits may not be available
        return kgen.generateKey().getEncoded();
    }
}
