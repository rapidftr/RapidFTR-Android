package com.rapidftr.view.fields;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.utils.CaptureHelper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class PhotoUploadBoxTest extends BaseViewSpec<PhotoUploadBox> {

    protected CaptureHelper captureHelper;
    protected Bitmap bitmap;
    protected ImageView imageView;

    @Before
    public void setUp() throws IOException {
        captureHelper = mock(CaptureHelper.class);
        bitmap = mock(Bitmap.class);
        imageView = mock(ImageView.class);

        view = spy((PhotoUploadBox) LayoutInflater.from(new RegisterChildActivity()).inflate(R.layout.form_photo_upload_box, null));
        doReturn(imageView).when(view).getImageView();
        when(captureHelper.getCaptureBitmap()).thenReturn(bitmap);

        view.captureHelper = captureHelper;
    }

    @Test
    public void testSaveShouldDeleteCaptures() throws IOException, JSONException, GeneralSecurityException {
        view.initialize(field, child);
        view.saveCapture();
        verify(captureHelper).deleteCaptures();
    }

    @Test
    public void testSaveCaptureShouldSaveBitmap() throws IOException, JSONException, GeneralSecurityException {
        view.initialize(field, child);
        view.saveCapture();
        verify(captureHelper).save(eq(bitmap), anyString());
    }

    @Test
    public void testSaveCaptureShouldSaveThumbnail() throws IOException, JSONException, GeneralSecurityException {
        view.initialize(field, child);
        view.saveCapture();
        verify(captureHelper).saveThumbnail(eq(bitmap), anyString());
    }

    @Test
    public void testSaveCaptureShouldSaveFileNameInChild() throws JSONException, IOException, GeneralSecurityException {
        view.initialize(field, child);

        String fileName = "random_file_name";
        doReturn(fileName).when(view).createCaptureFileName();

        view.saveCapture();
        assertThat(child.getString(field.getId()), equalTo("random_file_name"));
    }

    @Test
    public void testPaintThumbnail() throws JSONException, IOException {
        view.initialize(field, child);
        child.put(field.getId(), "test_image");
        when(captureHelper.loadThumbnailOrDefault("test_image")).thenReturn(bitmap);

        view.repaint();
        verify(imageView).setImageBitmap(bitmap);
    }

}
