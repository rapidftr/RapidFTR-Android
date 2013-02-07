package com.rapidftr.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.view.fields.PhotoUploadBox;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class EncryptImageAsyncTaskTest {
    private PhotoCaptureHelper photoCaptureHelper = mock(PhotoCaptureHelper.class);
    private Context context = new Activity();
    private Bitmap bitmap = mock(Bitmap.class);
    private PhotoUploadBox photoUploadBox = mock(PhotoUploadBox.class);

    @Test
    public void testEncryptAndSaveImage() throws Exception {
        String fileName = "random";
        EncryptImageAsyncTask asyncTask = new EncryptImageAsyncTask(context, photoCaptureHelper, bitmap, fileName, photoUploadBox, 90);
        doReturn(bitmap).when(photoCaptureHelper).rotateBitmap(bitmap, 90);
        AsyncTask<Void, Integer, Boolean> task = asyncTask.execute();
        assertTrue(task.get());
        verify(photoCaptureHelper).saveThumbnail(bitmap, fileName);
        verify(photoCaptureHelper).savePhoto(bitmap, fileName);
        verify(bitmap).recycle();
        verify(photoUploadBox).repaint();
    }

    @Test
    public void testEncryptShouldReturnFalseIfSaveFails() throws Exception {
        String fileName = "random";
        EncryptImageAsyncTask asyncTask = new EncryptImageAsyncTask(context, photoCaptureHelper, bitmap, fileName, photoUploadBox, 180);
        doReturn(bitmap).when(photoCaptureHelper).rotateBitmap(bitmap, 180);
        doThrow(new RuntimeException()).when(photoCaptureHelper).saveThumbnail(bitmap, fileName);
        AsyncTask<Void, Integer, Boolean> task = asyncTask.execute();
        assertFalse(task.get());
        verify(photoCaptureHelper).saveThumbnail(bitmap, fileName);
        verify(photoCaptureHelper,never()).savePhoto(bitmap, fileName);
        verify(bitmap).recycle();
        verify(photoUploadBox).repaint();
    }
}
