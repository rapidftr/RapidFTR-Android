package com.rapidftr.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.utils.CaptureHelper;
import com.rapidftr.view.fields.PhotoUploadBox;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class EncryptImageAsyncTaskTest {
    private CaptureHelper captureHelper = mock(CaptureHelper.class);
    private Context context = new Activity();
    private Bitmap bitmap = mock(Bitmap.class);
    private PhotoUploadBox photoUploadBox = mock(PhotoUploadBox.class);

    @Test
    public void testEncryptAndSaveImage() throws Exception {
        String fileName = "random";
        EncryptImageAsyncTask asyncTask = new EncryptImageAsyncTask(context, captureHelper, bitmap, fileName, photoUploadBox);
        AsyncTask<Void, Void, Boolean> task = asyncTask.execute();
        assertTrue(task.get());
        verify(captureHelper).saveThumbnail(bitmap, fileName);
        verify(captureHelper).savePhoto(bitmap, fileName);
        verify(bitmap).recycle();
        verify(photoUploadBox).repaint();
    }

    @Test
    public void testEncryptShouldReturnFalseIfSaveFails() throws Exception {
        String fileName = "random";
        EncryptImageAsyncTask asyncTask = new EncryptImageAsyncTask(context, captureHelper, bitmap, fileName, photoUploadBox);
        doThrow(new RuntimeException()).when(captureHelper).saveThumbnail(bitmap, fileName);
        AsyncTask<Void, Void, Boolean> task = asyncTask.execute();
        assertFalse(task.get());
        verify(captureHelper).saveThumbnail(bitmap, fileName);
        verify(captureHelper,never()).savePhoto(bitmap, fileName);
        verify(bitmap).recycle();
        verify(photoUploadBox).repaint();
    }
}
