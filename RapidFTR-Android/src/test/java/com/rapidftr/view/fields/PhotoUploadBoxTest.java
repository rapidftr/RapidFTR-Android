package com.rapidftr.view.fields;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.activity.BaseChildActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.utils.CaptureHelper;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.json.JSONException;
import org.junit.Assert;
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
        when(captureHelper.getCapture()).thenReturn(bitmap);

        view.captureHelper = captureHelper;
    }

    @Test
    public void shouldStartCameraActivityWhenImageClickedAndViewIsEnabled() {
        view.initialize(field, child);
        doNothing().when(view).startCapture();

        view.setEnabled(true);
        view.getImageContainer().performClick();
        verify(view).startCapture();
    }

    @Test
    public void shouldShowFullPhotoWhenImageClickedAndViewIsDisabled(){
        view.initialize(field, child);
        doNothing().when(view).startCapture();

        view.setEnabled(false);
        view.getImageContainer().performClick();
        verify(view).showFullPhoto();

    }
    @Test
    public void shouldShowPhotoWhenImageClicked() throws Exception {
        view.initialize(field, child);
        doNothing().when(view).showFullPhoto();

        child.put(field.getId(), "random_file_name");
        view.getImageContainer().performClick();

        verify(view).onImageClick();
        verify(view).showFullPhoto();
    }

    @Test
    public void shouldShowImageNotAvailableToastIfViewIsDisabledAndTheImageIsNotAvailable(){
        view.initialize(field, child);
        view.setEnabled(false);
        view.getImageContainer().performClick();
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), equalTo(view.getContext().getString(R.string.photo_not_captured)));
    }

    @Test
    public void shouldSaveCaptureWhenCapturingSuccess() throws Exception {
        view.initialize(field, child);
        RapidFtrActivity activity = (RapidFtrActivity) view.getContext();
        doNothing().when(view).saveCapture();

        activity.onActivityResult(PhotoUploadBox.CAPTURE_IMAGE_REQUEST, Activity.RESULT_OK, null);
        verify(view).saveCapture();
    }

    public void shouldCheckIfSavePhotoIsCalledWhenSaveIsSelected() throws Exception {
        view.initialize(field, child);
        RapidFtrActivity activity = (RapidFtrActivity) view.getContext();
        doCallRealMethod().when(view).saveCapture();
        doNothing().when(captureHelper).savePhoto(bitmap,"");
        activity.onActivityResult(PhotoUploadBox.CAPTURE_IMAGE_REQUEST, Activity.RESULT_OK, null);
        verify(view).saveCapture();
        verify(captureHelper).savePhoto(bitmap, "name");
    }

    @Test
    public void shouldNotSaveCaptureWhenCapturingCancelled() throws Exception {
        view.initialize(field, child);
        RapidFtrActivity activity = (RapidFtrActivity) view.getContext();
        activity.onActivityResult(PhotoUploadBox.CAPTURE_IMAGE_REQUEST, Activity.RESULT_CANCELED, null);
        verify(view, never()).saveCapture();
    }

    @Test
    public void shouldSetCaptureTimeWhenStartCapture() throws Exception {
        doCallRealMethod().when(captureHelper).getTempCaptureFile();
        view.startCapture();
        verify(captureHelper).setCaptureTime();
    }

    @Test
    public void shouldDeleteCapturedImagesWhenRegistrationIsCancelled() throws Exception {
        view.initialize(field, child);
        RapidFtrActivity activity = (RapidFtrActivity) view.getContext();
        doNothing().when(view).deleteCapture();

        activity.onActivityResult(BaseChildActivity.CLOSE_ACTIVITY, 999, null);
        verify(view).deleteCapture();
    }

    @Test
    public void testSaveCaptureShouldSaveBitmap() throws IOException, JSONException, GeneralSecurityException {
        view.initialize(field, child);
        view.saveCapture();
        verify(captureHelper).savePhoto(eq(bitmap), anyString());
    }

    @Test
    public void testSaveShouldDeleteCaptures() throws IOException, JSONException, GeneralSecurityException {
        view.initialize(field, child);
        view.saveCapture();
        verify(captureHelper).deleteCaptures();
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
    public void shouldOverwriteExistingFile() throws Exception {
        view.initialize(field, child);
        child.put(field.getId(), "random_file_name");
        assertThat(view.createCaptureFileName(), equalTo("random_file_name"));
    }

    @Test
    public void testPaintThumbnail() throws JSONException, IOException {
        view.initialize(field, child);
        child.put(field.getId(), "test_image");
        when(captureHelper.getThumbnailOrDefault("test_image")).thenReturn(bitmap);

        view.repaint();
        verify(imageView).setImageBitmap(bitmap);
    }

}
