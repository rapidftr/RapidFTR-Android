package com.rapidftr.utils;

import android.graphics.Bitmap;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class CaptureHelperTest {

    RapidFtrApplication application;
    CaptureHelper captureHelper;

    @Before
    public void setUp() {
        application = new RapidFtrApplication();
        captureHelper = spy(new CaptureHelper(application));
    }

    @Test
    public void testCaptureDirUnderRapidTRNoMedia() {
        String path = captureHelper.getPhotoDir().getAbsolutePath();
        assertThat(path, endsWith("/rapidftr/.nomedia"));
    }

    @Test
    public void testCatureFileUnderCaptureDir() {
        String path = captureHelper.getPhotoDir().getAbsolutePath();
        String file = captureHelper.getTempCaptureFile().getAbsolutePath();
        assertThat(file, startsWith(path));
    }

    @Test
    public void testSaveCaptureTimeInSharedPreferences() {
        long time1 = System.currentTimeMillis();
        captureHelper.setCaptureTime();
        long time2 = System.currentTimeMillis();

        long time = application.getSharedPreferences().getLong("capture_start_time", 0);
        assertTrue(time >= time1 && time <= time2);
    }

    @Test
    public void testGetCaptureTimeFromSharedPreferences() {
        Calendar expected = Calendar.getInstance();
        expected.setTimeInMillis(500);

        application.getSharedPreferences().edit().putLong("capture_start_time", 500).commit();
        Calendar actual = captureHelper.getCaptureTime();

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testReturnDefaultThumbnail() throws Exception {
        doThrow(RuntimeException.class).when(captureHelper).loadThumbnail("random_file");
        Bitmap bitmap = captureHelper.getThumbnailOrDefault("random_file");
        assertTrue(sameBitmap(bitmap, captureHelper.getDefaultThumbnail()));
    }

    @Test
    public void testReturnOriginalThumbnail() throws Exception {
        Bitmap expected = mock(Bitmap.class);
        doReturn(expected).when(captureHelper).loadThumbnail("random_file");

        Bitmap actual = captureHelper.getThumbnailOrDefault("random_file");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testSaveThumbnailShouldResizeAndSave() throws Exception {
        Bitmap original = mock(Bitmap.class), expected = mock(Bitmap.class);
        doReturn(expected).when(captureHelper).scaleImageTo(original, 96, 96);
        doNothing().when(captureHelper).save(expected, "random_file_thumb");

        captureHelper.saveThumbnail(original, "random_file");
        verify(captureHelper).save(expected, "random_file_thumb");
    }

    @Test
    public void testSaveActualImageShouldResizeAndSave() throws Exception {
        Bitmap original = mock(Bitmap.class), expected = mock(Bitmap.class);
        doReturn(expected).when(captureHelper).scaleImageTo(original, 300, 300);
        doNothing().when(captureHelper).save(expected, "random_file");

        captureHelper.savePhoto(original, "random_file");
        verify(captureHelper).save(expected, "random_file");
    }

    @Test
    public void testSavePhotoAndCompress() throws Exception {
        Bitmap bitmap = mock(Bitmap.class);
        File file = new File(captureHelper.getPhotoDir(), "random_file.jpg");
        OutputStream out = mock(OutputStream.class);

        doReturn(out).when(captureHelper).getCipherOutputStream(eq(file));
        captureHelper.save(bitmap, "random_file");
        verify(bitmap).compress(Bitmap.CompressFormat.JPEG, 85, out);
        verify(out).close();
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
