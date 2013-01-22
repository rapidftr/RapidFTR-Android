package com.rapidftr.task;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.adapter.ThumbnailDrawable;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class AssignThumbnailAsyncTaskTest {

    @Mock ImageView imageView;
    @Mock
    PhotoCaptureHelper photoCaptureHelper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldUseCaptureHelperToLoadImage(){
        AssignThumbnailAsyncTask task = new AssignThumbnailAsyncTask(imageView, photoCaptureHelper);
        String filename = "someFilename";

        task.doInBackground(filename);

         verify(photoCaptureHelper).getThumbnailOrDefault(filename);
    }

    @Test
    public void shouldAssignImageToViewOnPostExecute(){
        AssignThumbnailAsyncTask task = new AssignThumbnailAsyncTask(imageView, photoCaptureHelper);
        ThumbnailDrawable thumbnailDrawable = mock(ThumbnailDrawable.class);
        given(imageView.getDrawable()).willReturn(thumbnailDrawable);
        given(thumbnailDrawable.getAssignThumbnailAsyncTask()).willReturn(task);
        Bitmap bitmap = mock(Bitmap.class);

        task.onPostExecute(bitmap);

        verify(imageView).setImageBitmap(bitmap);
    }
}
