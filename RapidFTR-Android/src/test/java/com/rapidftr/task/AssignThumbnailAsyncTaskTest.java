package com.rapidftr.task;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.rapidftr.utils.CaptureHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static android.graphics.Bitmap.createBitmap;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AssignThumbnailAsyncTaskTest {

    @Mock ImageView imageView;
    @Mock CaptureHelper captureHelper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldUseCaptureHelperToLoadImage(){
        AssignThumbnailAsyncTask task = new AssignThumbnailAsyncTask(imageView, captureHelper);

        task.doInBackground("someFilename");

         verify(captureHelper).getThumbnailOrDefault("someFileName");
    }

    @Test
    public void shouldAssignImageToViewOnPostExecute(){
        AssignThumbnailAsyncTask task = new AssignThumbnailAsyncTask(imageView, captureHelper);

        Bitmap bitmap = createBitmap(null);
        task.onPostExecute(bitmap);

        verify(imageView).setImageBitmap(bitmap);
    }
}
