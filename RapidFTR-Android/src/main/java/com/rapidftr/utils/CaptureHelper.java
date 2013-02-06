package com.rapidftr.utils;

import android.content.Context;
import android.os.Environment;
import com.rapidftr.RapidFtrApplication;
import lombok.RequiredArgsConstructor;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CaptureHelper {
    protected RapidFtrApplication application;

    public CaptureHelper(RapidFtrApplication context){
        application = context;
    }

    public File getDir() {
        File baseDir = getExternalStorageDir();
        if (!baseDir.canWrite())
            baseDir = getInternalStorageDir();

        File picDir = new File(baseDir, ".nomedia");
        picDir.mkdirs();
        return picDir;
    }

    protected File getExternalStorageDir() {
        File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File picDir = new File(extDir, "rapidftr");

	    picDir.mkdirs();
	    return picDir;
    }

    protected File getInternalStorageDir() {
        return application.getDir("capture", Context.MODE_PRIVATE);
    }

    /**
     * We need to generate a Cipher key from the DbKey (since we cannot just directly use the dbKey as password)
     * So we generate the following:
     * Salt   - salt is not supposed to be secure, it is just for avoiding dictionary based attacks
     * salt is almost always stored along with the encrypted data
     * so in our case we use the file name as a seed for generating a random salt
     * Key    - 256 bit cipher key generated from Password and Salt
     * IV     - Initialization vector, some random number to begin with, again generated from file name
     * Cipher - Cipher created from key and IV
     * <p/>
     * Reference: http://nelenkov.blogspot.in/2012/04/using-password-based-encryption-on.html
     */
    protected Cipher getCipher(int mode, String fileName) throws GeneralSecurityException {
        String password = application.getCurrentUser().getDbKey();
        int iterationCount = 100, saltLength = 8, keyLength = 256;

        SecureRandom random = new SecureRandom();
        random.setSeed(fileName.getBytes());
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(mode, key, ivParams);
        return cipher;
    }

    protected OutputStream getCipherOutputStream(File file) throws GeneralSecurityException, IOException {
        return new CipherOutputStream(new FileOutputStream(file), getCipher(Cipher.ENCRYPT_MODE, file.getName()));
    }

    protected InputStream getCipherInputStream(File file) throws GeneralSecurityException, IOException {
        return new CipherInputStream(new FileInputStream(file), getCipher(Cipher.DECRYPT_MODE, file.getName()));
    }

    public File getFile(String fileNameWithoutExtension, String extension) throws FileNotFoundException {
        File file = new File(getDir(), fileNameWithoutExtension + extension);
        if (!file.exists())
            throw new FileNotFoundException();
        return file;
    }
}
