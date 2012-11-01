package com.rapidftr.view.fields;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.rapidftr.R;
import org.json.JSONException;

public class PhotoUploadBox extends BaseView {

    public static final int CAPTURE_IMAGE_REQUEST = 100;

    public PhotoUploadBox(Context context) {
        super(context);
    }

    public PhotoUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Button getCaptureButton() {
        return (Button) findViewById(R.id.capture);
    }

    public void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity) getContext()).getIntent().putExtra("field_id", formField.getId());
        ((Activity) getContext()).startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
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

        repaint();
    }

    public void setImage(Bitmap image) throws JSONException {
        Bitmap thumbnail = getThumbnail(image);
        child.put(formField.getId(), "placeholder image key");
        child.setThumbnail(formField.getId(), thumbnail);
        repaint();
    }

    protected Bitmap getThumbnail(Bitmap image) {
        return Bitmap.createScaledBitmap(image, 96, 96, false);
    }

    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.thumbnail);
    }

    public void repaint() throws JSONException {
        Bitmap bitmap = child.getThumbnail(formField.getId());
        getImageView().setImageBitmap(bitmap);
    }
}
