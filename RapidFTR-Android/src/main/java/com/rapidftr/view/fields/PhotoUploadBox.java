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
import android.widget.TextView;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.activity.ViewPhotoActivity;
import com.rapidftr.utils.CaptureHelper;
import org.json.JSONException;

import java.util.UUID;

public class PhotoUploadBox extends BaseView implements RapidFtrActivity.ResultListener {

    public static final int CAPTURE_IMAGE_REQUEST = 100;

    protected CaptureHelper captureHelper;

    public PhotoUploadBox(Context context) {
        super(context);
        captureHelper = new CaptureHelper(((RapidFtrActivity) context).getContext());
    }

    public PhotoUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        captureHelper = new CaptureHelper(((RapidFtrActivity) context).getContext());
    }

    @Override
    protected void initialize() throws JSONException {
        super.initialize();

        RapidFtrActivity activity = (RapidFtrActivity) getContext();
        activity.addResultListener(CAPTURE_IMAGE_REQUEST, this);
        activity.addResultListener(RegisterChildActivity.CLOSE_ACTIVITY, this);

        getImageContainer().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick();
            }
        });
        repaint();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK)
                    saveCapture();
                break;
            case RegisterChildActivity.CLOSE_ACTIVITY:
                deleteCapture();
                break;
        }
    }

    protected void deleteCapture() {
        if (!child.optBoolean("saved", false)) {
            // TODO: Delete taken image
        }
    }

    public View getImageContainer() {
        return (View) findViewById(R.id.capture);
    }

    public void onImageClick() {
        if (child.has(formField.getId())) {
            showFullPhoto();
        } else {
            startCapture();
        }
    }

    protected void showFullPhoto() {
        Activity context = (Activity) getContext();
        try {
            Intent intent = new Intent(context, ViewPhotoActivity.class);
            intent.putExtra("file_name", child.getString(formField.getId()));

            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.photo_view_error, Toast.LENGTH_LONG).show();
        }
    }

    public void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureHelper.getTempCaptureFile()));

        Activity context = (Activity) getContext();
        captureHelper.setCaptureTime();
        context.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    public void saveCapture() {
        try {
            Bitmap bitmap = captureHelper.getCapture();
            captureHelper.deleteCaptures();

            String fileName = createCaptureFileName();
            captureHelper.savePhoto(bitmap, fileName);
            captureHelper.saveThumbnail(bitmap, fileName);
            child.put(formField.getId(), fileName);

            bitmap.recycle();
            repaint();
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.photo_capture_error, Toast.LENGTH_LONG);
        }
    }

    protected String createCaptureFileName() {
        String fileName = child.optString(formField.getId());
        return fileName != null ? fileName : UUID.randomUUID().toString();
    }

    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.thumbnail);
    }

    protected TextView getImageCaption() {
        return (TextView) findViewById(R.id.caption);
    }

    public void repaint() throws JSONException {
        Bitmap bitmap = captureHelper.getThumbnailOrDefault(child.optString(formField.getId()));
        getImageView().setImageBitmap(bitmap);
        getImageCaption().setText(child.has(formField.getId()) ? R.string.photo_view : R.string.photo_capture);
    }

}
