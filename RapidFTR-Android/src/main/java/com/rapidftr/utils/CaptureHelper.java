package com.rapidftr.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Cleanup;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Calendar;

import static android.graphics.BitmapFactory.decodeResource;

public class CaptureHelper {

    public File getCaptureDir() {
        File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File appDir = new File(extDir, "rapidftr");
        File picDir = new File(appDir, ".nomedia");

        picDir.mkdirs();
        return picDir;
    }

    public File getTempCaptureFile() {
        return new File(getCaptureDir(), "temp.jpg");
    }

    public void deleteTempCaptureFile() {
        File file = getTempCaptureFile();
        if (file.exists())
            file.delete();
    }

    public Bitmap getCaptureBitmap() throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(getTempCaptureFile().getAbsolutePath());
        int rotation = getCaptureRotation();

        return rotation > 0 ? rotateBitmap(bitmap, rotation) : bitmap;
    }

    protected Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,  true);
    }

    protected int getCaptureRotation() throws IOException {
        ExifInterface exif = new ExifInterface(getTempCaptureFile().getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    public void deleteCapturesAfter(Calendar since) {
        Calendar date = Calendar.getInstance();
        ContentResolver resolver = RapidFtrApplication.getInstance().getContentResolver();

        @Cleanup Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.ImageColumns.DATE_TAKEN,  MediaStore.Images.ImageColumns.DATA },
                null, null, null);

        while (cursor.moveToNext()) {
            date.setTimeInMillis(cursor.getLong(0));
            if (date.after(since))
                deleteCapture(cursor.getString(1));
        }
    }

    protected void deleteCapture(String data) {
        File image = new File(data);
        if (image.exists())
            image.delete();
    }

    public Bitmap getDefaultThumbnail() {
        return decodeResource(RapidFtrApplication.getInstance().getResources(), R.drawable.no_photo_clip);
    }

    public Bitmap createThumbnail(Bitmap image) {
        return Bitmap.createScaledBitmap(image, 96, 96, false);
    }

    public Bitmap save(Bitmap bitmap, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        File file = new File(getCaptureDir(), fileNameWithoutExtension + ".jpg");
        if (!file.exists())
            file.createNewFile();

        @Cleanup FileOutputStream outStream = new FileOutputStream(file);
        @Cleanup CipherOutputStream cipherOutputStream = new CipherOutputStream(outStream, getCipher(Cipher.ENCRYPT_MODE, fileNameWithoutExtension));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, cipherOutputStream);
        return bitmap;
    }

    public Bitmap saveThumbnail(Bitmap bitmap, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        return save(createThumbnail(bitmap), fileNameWithoutExtension + "_thumb");
    }

    public Bitmap loadThumbnail(String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        return load(fileNameWithoutExtension + "_thumb");
    }

    /**
     * We need to generate a Cipher key from the DbKey (since we cannot just directly use the dbKey as password)
     * So we generate the following:
     *    Salt   - salt is not supposed to be secure, it is just for avoiding dictionary based attacks
     *             salt is almost always stored along with the encrypted data
     *             so in our case we use the file name as a seed for generating a random salt
     *    Key    - 256 bit cipher key generated from Password and Salt
     *    IV     - Initialization vector, some random number to begin with, again generated from file name
     *    Cipher - Cipher created from key and IV
     *
     * Reference: http://nelenkov.blogspot.in/2012/04/using-password-based-encryption-on.html
     */
    protected Cipher getCipher(int mode, String fileName) throws GeneralSecurityException {
        String password = RapidFtrApplication.getInstance().getDbKey();
        int iterationCount = 100, saltLength = 8, keyLength = 256;

        SecureRandom random = new SecureRandom();
        random.setSeed(fileName.getBytes());
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(mode, key, ivParams);
        return cipher;
    }

    public Bitmap load(String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        File inFile = new File(getCaptureDir(), fileNameWithoutExtension + ".jpg");
        if (!inFile.exists())
            throw new FileNotFoundException();

        @Cleanup FileInputStream inputStream = new FileInputStream(inFile);
        @Cleanup CipherInputStream cipherInputStream = new CipherInputStream(inputStream, getCipher(Cipher.DECRYPT_MODE, fileNameWithoutExtension));
        return BitmapFactory.decodeStream(cipherInputStream);
    }

    public Bitmap loadThumbnailOrDefault(String fileNameWithoutExtension) {
        Bitmap bitmap = null;
        try {
            if (fileNameWithoutExtension != null) {
                bitmap = loadThumbnail(fileNameWithoutExtension);
            }
        } catch (Exception e) { }

        return bitmap != null ? bitmap : getDefaultThumbnail();
    }

}
