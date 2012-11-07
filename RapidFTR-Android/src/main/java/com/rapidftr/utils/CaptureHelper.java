package com.rapidftr.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

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

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class CaptureHelper {

    protected final RapidFtrApplication application;

    public File getPhotoDir() {
        File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File appDir = new File(extDir, "rapidftr");
        File picDir = new File(appDir, ".nomedia");

        picDir.mkdirs();
        return picDir;
    }

    public File getTempCaptureFile() {
        return new File(getPhotoDir(), "temp.jpg");
    }

    public Calendar getCaptureTime() {
        Calendar calendar = Calendar.getInstance();
        long time = application.getSharedPreferences().getLong("capture_start_time", System.currentTimeMillis());
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public void setCaptureTime() {
        application.getSharedPreferences().edit().putLong("capture_start_time", Calendar.getInstance().getTimeInMillis()).commit();
    }

    public Bitmap getCapture() throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(getTempCaptureFile().getAbsolutePath());
        int rotation = getCaptureRotation();

        return rotation > 0 ? rotateBitmap(bitmap, rotation) : bitmap;
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

    protected Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,  true);
    }

    public void deleteCaptures() {
        this.deleteTempCapture();
        this.deleteGalleryCaptures();
    }

    protected void deleteTempCapture() {
        File file = getTempCaptureFile();
        if (file.exists())
            file.delete();
    }

    protected void deleteGalleryCaptures() {
        Calendar from = getCaptureTime();
        Calendar capturedDate = Calendar.getInstance();
        ContentResolver resolver = application.getContentResolver();

        @Cleanup Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {BaseColumns._ID, MediaStore.Images.ImageColumns.DATE_TAKEN,  MediaStore.Images.ImageColumns.DATA },
                null, null, null);

        while (cursor != null && cursor.moveToNext()) {
            try {
                capturedDate.setTimeInMillis(cursor.getLong(1));
                if (capturedDate.after(from)) {
                    deleteGalleryCapture(cursor.getString(0), cursor.getString(2));
                }
            } catch (Exception e) {
                Toast.makeText(application, R.string.photo_gallery_delete_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void deleteGalleryCapture(String id, String completeFileName) {
        File image = new File(completeFileName);
        if (image.exists())
            image.delete();

        application.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + id, null);
    }

    public Bitmap getDefaultThumbnail() {
        return decodeResource(application.getResources(), R.drawable.no_photo_clip);
    }

    protected Bitmap createThumbnail(Bitmap image) {
        return Bitmap.createScaledBitmap(image, 96, 96, false);
    }

    public void savePhoto(Bitmap bitmap, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        File file = new File(getPhotoDir(), fileNameWithoutExtension + ".jpg");
        if (!file.exists())
            file.createNewFile();

        @Cleanup OutputStream outputStream = getCipherOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
    }

    public void saveThumbnail(Bitmap bitmap, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        savePhoto(createThumbnail(bitmap), fileNameWithoutExtension + "_thumb");
    }

    public Bitmap loadThumbnail(String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        return loadPhoto(fileNameWithoutExtension + "_thumb");
    }

    public Bitmap loadPhoto(String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        File file = new File(getPhotoDir(), fileNameWithoutExtension + ".jpg");
        if (!file.exists())
            throw new FileNotFoundException();

        @Cleanup InputStream inputStream = getCipherInputStream(file);
        return BitmapFactory.decodeStream(inputStream);
    }

    public Bitmap getThumbnailOrDefault(String fileNameWithoutExtension) {
        Bitmap bitmap = null;
        try {
            if (fileNameWithoutExtension != null) {
                bitmap = loadThumbnail(fileNameWithoutExtension);
            }
        } catch (Exception e) { }

        return bitmap != null ? bitmap : getDefaultThumbnail();
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
        String password = application.getDbKey();
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

    protected OutputStream getCipherOutputStream(File file) throws GeneralSecurityException, IOException {
        return new CipherOutputStream(new FileOutputStream(file), getCipher(Cipher.ENCRYPT_MODE, file.getName()));
    }

    protected InputStream getCipherInputStream(File file) throws GeneralSecurityException, IOException {
        return new CipherInputStream(new FileInputStream(file), getCipher(Cipher.DECRYPT_MODE, file.getName()));
    }

}
