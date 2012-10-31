package com.rapidftr.view.fields;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.rapidftr.R;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;

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
        child.put(formField.getId(), bitmapToBase64(thumbnail));
        repaint();
    }

    protected Bitmap getThumbnail(Bitmap image) {
        return Bitmap.createScaledBitmap(image, 96, 96, false);
    }

    protected String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 85, out);
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    protected Bitmap bitmapFromBase64(String image) {
        byte[] bitmap = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }

    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.thumbnail);
    }

    public void repaint() throws JSONException {
        if (child.has(formField.getId())) {
            Bitmap bitmap = bitmapFromBase64(child.getString(formField.getId()));
            getImageView().setImageBitmap(bitmap);
        }
    }
}
