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
import com.rapidftr.R;
import com.rapidftr.utils.BitmapUtil;
import org.json.JSONException;

public class PhotoUploadBox extends BaseView {

    public static final int CAPTURE_IMAGE_REQUEST = 100;

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

        repaint();
    }

    public View getCaptureButton() {
        return (View) findViewById(R.id.capture);
    }

    public void startCapture() {
        Activity context = (Activity) getContext();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(BitmapUtil.getTempStorageFile()));

        context.getIntent().putExtra("field_id", formField.getId());
        context.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.thumbnail);
    }

    public void repaint() throws JSONException {
        Bitmap bitmap = child.getThumbnail(formField.getId());
        getImageView().setImageBitmap(bitmap);
    }
}
