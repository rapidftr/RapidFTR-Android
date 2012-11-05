package com.rapidftr.view.fields;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.utils.CaptureHelper;
import org.json.JSONException;

import java.io.IOException;
import java.util.UUID;

public class PhotoUploadBox extends BaseView implements RapidFtrActivity.ResultListener {

    public static final int CAPTURE_IMAGE_REQUEST = 100;

    protected CaptureHelper captureHelper = new CaptureHelper();

    public PhotoUploadBox(Context context) {
        super(context);
    }

    public PhotoUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() throws JSONException {
        super.initialize();
        getCaptureButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startCapture();
            }
        });
        ((RapidFtrActivity) getContext()).addResultListener(CAPTURE_IMAGE_REQUEST, this);

        repaint();
    }

    @Override
    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                saveCapture();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.capture_photo_error, Toast.LENGTH_LONG);
            }
        }
    }

    public View getCaptureButton() {
        return (View) findViewById(R.id.capture);
    }

    public void startCapture() {
        Activity context = (Activity) getContext();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureHelper.getTempCaptureFile()));

        context.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    public void saveCapture() throws IOException, JSONException {
        Bitmap bitmap = captureHelper.getCaptureBitmap();
        captureHelper.deleteTempCaptureFile();

        String fileName = createCaptureFileName();
        captureHelper.save(bitmap, fileName);
        captureHelper.saveThumbnail(bitmap, fileName);
        child.put(formField.getId(), fileName);

        repaint();

        // TODO: Delete captures taken in gallery
    }

    protected String createCaptureFileName() {
        return UUID.randomUUID().toString();
    }

    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.thumbnail);
    }

    public void repaint() throws JSONException {
        Bitmap bitmap = captureHelper.loadThumbnailOrDefault(child.optString(formField.getId()));
        getImageView().setImageBitmap(bitmap);
    }

}
