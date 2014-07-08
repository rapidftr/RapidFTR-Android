package com.rapidftr.utils;

import android.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

public class EncryptionUtil {

    public static final Integer KEY_ITERATION_COUNT = 100;
    public static final Integer KEY_LENGTH = 128;
    public static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final String SECRET_KEY_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Reference: http://nelenkov.blogspot.in/2012/04/using-password-based-encryption-on.html
     * NOTE: Using the seed as both Salt & IV, since we have no space to store the Salt & IV in the encrypted data
     */
    public static Cipher getCipher(String password, String seed, int mode) throws IOException, GeneralSecurityException {
        byte salt[] = seed.getBytes();
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, KEY_ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, SECRET_KEY_ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] iv = paddedByteArray(seed, cipher.getBlockSize());
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, key, ivSpec);
        return cipher;
    }

    public static String encrypt(String password, String seed, String textToEncrypt) throws GeneralSecurityException, IOException {
        Cipher cipher = getCipher(password, seed, Cipher.ENCRYPT_MODE);
        return Base64.encodeToString(cipher.doFinal(textToEncrypt.getBytes()), Base64.DEFAULT);
    }

    public static String decrypt(String password, String seed, String encrypted) throws GeneralSecurityException, IOException {
        Cipher cipher = getCipher(password, seed, Cipher.DECRYPT_MODE);
        return new String(cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT)));
    }

    public static OutputStream getCipherOutputStream(File file, String password) throws GeneralSecurityException, IOException {
        return new CipherOutputStream(new FileOutputStream(file), getCipher(password, file.getName(), Cipher.ENCRYPT_MODE));
    }

    public static InputStream getCipherInputStream(File file, String password) throws GeneralSecurityException, IOException {
        return new CipherInputStream(new FileInputStream(file), getCipher(password, file.getName(), Cipher.DECRYPT_MODE));
    }

    public static byte[] paddedByteArray(String str, int size) {
        byte[] dst = new byte[size];
        byte[] src = str.getBytes();
        int length = str.length() > size ? size : str.length();
        System.arraycopy(src, 0, dst, 0, length);
        return dst;
    }

}
