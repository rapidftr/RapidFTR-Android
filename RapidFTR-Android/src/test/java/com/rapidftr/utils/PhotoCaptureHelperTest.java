package com.rapidftr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Environment;
import com.google.common.io.Files;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.robolectric.Robolectric;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Calendar;

import static com.rapidftr.RapidFtrApplication.*;
import static com.rapidftr.utils.PhotoCaptureHelper.QUALITY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class PhotoCaptureHelperTest {

    RapidFtrApplication application;
    PhotoCaptureHelper photoCaptureHelper;

    @Before
    public void setUp() {
        application = mock(RapidFtrApplication.class);
        when(application.getSharedPreferences()).thenReturn(Robolectric.application.getSharedPreferences(APP_IDENTIFIER, MODE_PRIVATE));
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
    @Ignore // TODO: Failing in Robolectric 2.0
    public void testReturnDefaultThumbnail() throws Exception {
        doThrow(RuntimeException.class).when(photoCaptureHelper).loadThumbnail("random_file");
        Bitmap bitmap = photoCaptureHelper.getThumbnailOrDefault("random_file");
        assertTrue(sameBitmap(bitmap, photoCaptureHelper.getDefaultThumbnail()));
    }

    @Test
    public void testReturnOriginalThumbnail() throws Exception {
        Bitmap expected = mock(Bitmap.class);
        doReturn(expected).when(photoCaptureHelper).loadThumbnail("random_file");
        doReturn(new File("random_file.jpg")).when(photoCaptureHelper).getFile("random_file", ".jpg");

        Bitmap actual = photoCaptureHelper.getThumbnailOrDefault("random_file");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testSaveThumbnailShouldResizeAndSave() throws Exception {
        Bitmap original = mock(Bitmap.class), scaled = mock(Bitmap.class), rotated = mock(Bitmap.class);
        doReturn(scaled).when(photoCaptureHelper).resizeImageTo(original, 96, 96);
	    doReturn(rotated).when(photoCaptureHelper).rotateBitmap(scaled, 90);
	    User user = mock(User.class);
	    doReturn(user).when(application).getCurrentUser();
	    doReturn("key").when(user).getDbKey();
        doNothing().when(photoCaptureHelper).save(rotated, "random_file_thumb", QUALITY, "key");

        photoCaptureHelper.saveThumbnail(original, 90, "random_file");
        verify(photoCaptureHelper).save(rotated, "random_file_thumb", QUALITY, "key");
    }

    @Test
    public void testSaveActualImageShouldResizeAndSave() throws Exception {
        Bitmap original = mock(Bitmap.class), scaled = mock(Bitmap.class), rotated = mock(Bitmap.class);
        doReturn(scaled).when(photoCaptureHelper).scaleImageTo(original, 475, 635);
	    doReturn(rotated).when(photoCaptureHelper).rotateBitmap(scaled, 180);
	    User user = mock(User.class);
	    doReturn(user).when(application).getCurrentUser();
	    doReturn("key").when(user).getDbKey();
        doNothing().when(photoCaptureHelper).save(rotated, "random_file", QUALITY, "key");

        photoCaptureHelper.savePhoto(original, 180, "random_file");
        verify(photoCaptureHelper).save(rotated, "random_file", QUALITY, "key");
	    verify(scaled).recycle();
	    verify(rotated).recycle();
    }

    @Test
    public void testSavePhotoAndCompress() throws Exception {
        Bitmap bitmap = mock(Bitmap.class);
        File file = new File(photoCaptureHelper.getDir(), "random_file.jpg");
        OutputStream out = mock(OutputStream.class);

        doReturn(out).when(photoCaptureHelper).getCipherOutputStream(eq(file), anyString());
        doReturn(mock(User.class)).when(application).getCurrentUser();
        photoCaptureHelper.save(bitmap, "random_file", QUALITY, "key");
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

	@Test
	public void testScaleImagePreserveAspectRatioHorizontally() throws Exception {
		Bitmap bitmap = mock(Bitmap.class);
		int maxWidth = 100, maxHeight = 100;
		int givenWidth = 300, givenHeight = 200;
		int expectedWidth = 100, expectedHeight = 66;

		given(bitmap.getWidth()).willReturn(givenWidth);
		given(bitmap.getHeight()).willReturn(givenHeight);
		doReturn(bitmap).when(photoCaptureHelper).resizeImageTo(bitmap, expectedWidth, expectedHeight);

		// If you get exception here - then it means resizeImageTo was not called with proper passing arguments
		assertThat(bitmap, equalTo(photoCaptureHelper.scaleImageTo(bitmap, maxWidth, maxHeight)));
	}

	@Test
	public void testScaleImagePreserveAspectRatioVertically() throws Exception {
		Bitmap bitmap = mock(Bitmap.class);
		int maxWidth = 100, maxHeight = 100;
		int givenWidth = 200, givenHeight = 300;
		int expectedWidth = 66, expectedHeight = 100;

		given(bitmap.getWidth()).willReturn(givenWidth);
		given(bitmap.getHeight()).willReturn(givenHeight);
		doReturn(bitmap).when(photoCaptureHelper).resizeImageTo(bitmap, expectedWidth, expectedHeight);

		// If you get exception here - then it means resizeImageTo was not called with proper passing arguments
		assertThat(bitmap, equalTo(photoCaptureHelper.scaleImageTo(bitmap, maxWidth, maxHeight)));
	}

	@Test
	public void testScaleImagePreserveAspectRatioHorizontally2() throws Exception {
		Bitmap bitmap = mock(Bitmap.class);
		int maxWidth = 100, maxHeight = 100;
		int givenWidth = 200, givenHeight = 50;
		int expectedWidth = 100, expectedHeight = 25;

		given(bitmap.getWidth()).willReturn(givenWidth);
		given(bitmap.getHeight()).willReturn(givenHeight);
		doReturn(bitmap).when(photoCaptureHelper).resizeImageTo(bitmap, expectedWidth, expectedHeight);

		// If you get exception here - then it means resizeImageTo was not called with proper passing arguments
		assertThat(bitmap, equalTo(photoCaptureHelper.scaleImageTo(bitmap, maxWidth, maxHeight)));
	}

	@Test
	public void testScaleImagePreserveAspectRatioVertically2() throws Exception {
		Bitmap bitmap = mock(Bitmap.class);
		int maxWidth = 100, maxHeight = 100;
		int givenWidth = 50, givenHeight = 200;
		int expectedWidth = 25, expectedHeight = 100;

		given(bitmap.getWidth()).willReturn(givenWidth);
		given(bitmap.getHeight()).willReturn(givenHeight);
		doReturn(bitmap).when(photoCaptureHelper).resizeImageTo(bitmap, expectedWidth, expectedHeight);

		// If you get exception here - then it means resizeImageTo was not called with proper passing arguments
		assertThat(bitmap, equalTo(photoCaptureHelper.scaleImageTo(bitmap, maxWidth, maxHeight)));
	}

    @Test
    public void shouldEncryptTheGivenEncryptedPhotoUsingNewKey() throws IOException, GeneralSecurityException {
        File mockFile = mock(File.class);
        File mockThumbnailFile = mock(File.class);
        doReturn(mockFile).when(photoCaptureHelper).getFile("photo_name", ".jpg");
        doReturn(mockThumbnailFile).when(photoCaptureHelper).getFile("photo_name_thumb", ".jpg");
        InputStream mockInputStream = mock(InputStream.class);
        InputStream mockThumbnailInputStream = mock(InputStream.class);
        Bitmap mockBitmap = mock(Bitmap.class);
        Bitmap mockThumbnailBitmap = mock(Bitmap.class);

        doReturn(mockInputStream).when(photoCaptureHelper).getCipherInputStream(mockFile, "oldKey");
        doReturn(mockThumbnailInputStream).when(photoCaptureHelper).getCipherInputStream(mockThumbnailFile, "oldKey");
        doReturn(mockBitmap).when(photoCaptureHelper).decodeStreamToBitMap(mockInputStream);
        doReturn(mockThumbnailBitmap).when(photoCaptureHelper).decodeStreamToBitMap(mockThumbnailInputStream);

        doNothing().when(photoCaptureHelper).save(Matchers.<Bitmap>any(), anyString(), anyInt(), anyString());

        photoCaptureHelper.convertPhoto("photo_name", "oldKey", "newKey");
        verify(photoCaptureHelper).save(mockBitmap, "photo_name", PhotoCaptureHelper.QUALITY, "newKey");
        verify(photoCaptureHelper).save(mockThumbnailBitmap, "photo_name_thumb", PhotoCaptureHelper.QUALITY, "newKey");
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
