package com.rapidftr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Environment;
import com.google.common.io.Files;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class PhotoCaptureHelperTest {

    RapidFtrApplication application;
    PhotoCaptureHelper photoCaptureHelper;

    @Before
    public void setUp() {
        application = spy(new RapidFtrApplication());
        photoCaptureHelper = spy(new PhotoCaptureHelper(application));
    }

    @Test
    public void testCaptureUnderNoMedia() {
        String path = photoCaptureHelper.getDir().getAbsolutePath();
        assertThat(path, endsWith("/.nomedia"));
    }

    @Test
    public void testCaptureUnderSDCard() {
        File file = Environment.getExternalStorageDirectory();
        doReturn(file).when(photoCaptureHelper).getExternalStorageDir();

        File result = photoCaptureHelper.getDir();
        assertThat(result.getParentFile(), equalTo(file));

    }

    @Test
    public void testCaptureUnderInternalStorage() {
        File file = mock(File.class);
        doReturn(false).when(file).canWrite();
        doReturn(file).when(photoCaptureHelper).getExternalStorageDir();

        File file2 = new File(Environment.getExternalStorageDirectory(), "internal");
        doReturn(file2).when(application).getDir("capture", Context.MODE_PRIVATE);

        File result = photoCaptureHelper.getDir();
        assertThat(result.getParentFile(), equalTo(file2));
    }

    @Test
    public void testCaptureDirUnderSDCard() {
        Environment.getExternalStorageState();
    }

    @Test
    public void testCatureFileUnderCaptureDir() {
        String path = photoCaptureHelper.getDir().getAbsolutePath();
        String file = photoCaptureHelper.getTempCaptureFile().getAbsolutePath();
        assertThat(file, startsWith(path));
    }

    @Test
    public void testSaveCaptureTimeInSharedPreferences() {
        long time1 = System.currentTimeMillis();
        photoCaptureHelper.setCaptureTime();
        long time2 = System.currentTimeMillis();

        long time = application.getSharedPreferences().getLong("capture_start_time", 0);
        assertTrue(time >= time1 && time <= time2);
    }

    @Test
    public void testGetCaptureTimeFromSharedPreferences() {
        Calendar expected = Calendar.getInstance();
        expected.setTimeInMillis(500);

        application.getSharedPreferences().edit().putLong("capture_start_time", 500).commit();
        Calendar actual = photoCaptureHelper.getCaptureTime();

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testReturnDefaultThumbnail() throws Exception {
        doThrow(RuntimeException.class).when(photoCaptureHelper).loadThumbnail("random_file");
        Bitmap bitmap = photoCaptureHelper.getThumbnailOrDefault("random_file");
        assertTrue(sameBitmap(bitmap, photoCaptureHelper.getDefaultThumbnail()));
    }

    @Test
    public void testReturnOriginalThumbnail() throws Exception {
        Bitmap expected = mock(Bitmap.class);
        doReturn(expected).when(photoCaptureHelper).loadThumbnail("random_file");

        Bitmap actual = photoCaptureHelper.getThumbnailOrDefault("random_file");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testSaveThumbnailShouldResizeAndSave() throws Exception {
        Bitmap original = mock(Bitmap.class), expected = mock(Bitmap.class);
        doReturn(expected).when(photoCaptureHelper).scaleImageTo(original, 96, 96);
        doReturn(expected).when(photoCaptureHelper).rotateBitmap(expected, 90);
        doNothing().when(photoCaptureHelper).save(expected, "random_file_thumb");

        photoCaptureHelper.saveThumbnail(original, 90, "random_file");
        verify(photoCaptureHelper).save(expected, "random_file_thumb");
    }

    @Test
    public void testSaveActualImageShouldResizeAndSave() throws Exception {
        Bitmap original = mock(Bitmap.class), expected = mock(Bitmap.class);
        doReturn(expected).when(photoCaptureHelper).scaleImageTo(original, 475, 635);
        doReturn(expected).when(photoCaptureHelper).rotateBitmap(expected, 180);
        doNothing().when(photoCaptureHelper).save(expected, "random_file");
        photoCaptureHelper.savePhoto(original, 180, "random_file");
        verify(photoCaptureHelper).save(expected, "random_file");
    }

    @Test
    public void testSavePhotoAndCompress() throws Exception {
        Bitmap bitmap = mock(Bitmap.class);
        File file = new File(photoCaptureHelper.getDir(), "random_file.jpg");
        OutputStream out = mock(OutputStream.class);

        doReturn(out).when(photoCaptureHelper).getCipherOutputStream(eq(file));
        photoCaptureHelper.save(bitmap, "random_file");
        verify(bitmap).compress(Bitmap.CompressFormat.JPEG, 85, out);
        verify(out).close();
    }

    @Test
    public void testShouldReturnRotationInfoOfPicture() throws IOException {
        ExifInterface mockExifInterface = mock(ExifInterface.class);
        doReturn(mockExifInterface).when(photoCaptureHelper).getExifInterface();
        doReturn(ExifInterface.ORIENTATION_ROTATE_90).when(mockExifInterface).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotation = photoCaptureHelper.getPictureRotation();
        assertEquals(90, rotation);
    }

    @After
    public void resetSharedDirectory() {
        try {
            Files.deleteRecursively(Environment.getExternalStorageDirectory());
            Environment.getExternalStorageDirectory().mkdir();
        } catch (IOException e) {
            // Do nothing
        }
    }

    protected boolean sameBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }
}
