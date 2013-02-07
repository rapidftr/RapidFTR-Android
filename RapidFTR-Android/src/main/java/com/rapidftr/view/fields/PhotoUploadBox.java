package com.rapidftr.view.fields;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.activity.ViewPhotoActivity;
import com.rapidftr.task.EncryptImageAsyncTask;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;

import static com.rapidftr.activity.BaseChildActivity.CLOSE_ACTIVITY;

public class PhotoUploadBox extends BaseView implements RapidFtrActivity.ResultListener {

    public static final int CAPTURE_IMAGE_REQUEST = 100;
    private final String PHOTO_KEYS = "photo_keys";

    protected PhotoCaptureHelper photoCaptureHelper;
    private boolean enabled;

    public PhotoUploadBox(Context context) {
        super(context);
        photoCaptureHelper = new PhotoCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    public PhotoUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        photoCaptureHelper = new PhotoCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    @Override
    protected void initialize() throws JSONException {
        super.initialize();

        RapidFtrActivity activity = (RapidFtrActivity) getContext();
        activity.addResultListener(CAPTURE_IMAGE_REQUEST, this);
        activity.addResultListener(CLOSE_ACTIVITY, this);

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
            case CLOSE_ACTIVITY:
                deleteCapture();
                break;
        }
    }

    @Override
    public void setEnabled(boolean isEnabled){
       this.enabled = isEnabled ;
    }

    protected void deleteCapture() {
        if (!child.optBoolean("saved", false)) {
            // TODO: Delete taken image
        }
    }

    public View getImageContainer() {
        return findViewById(R.id.capture);
    }

    public void onImageClick() {
        if (enabled) {
            startCapture();
        } else {
            showFullPhoto();
        }
    }

    protected void showFullPhoto() {
        Activity context = (Activity) getContext();
        try {
            String fileName = (String) child.opt(formField.getId());
            if (fileName == null) {
                Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_not_captured, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context, ViewPhotoActivity.class);
                intent.putExtra("file_name", fileName);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_view_error, Toast.LENGTH_LONG).show();
        }
    }

    public void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoCaptureHelper.getTempCaptureFile()));

        RapidFtrActivity context = (RapidFtrActivity) getContext();
        photoCaptureHelper.setCaptureTime();
        context.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    public void saveCapture() {
        try {
            Bitmap bitmap = photoCaptureHelper.getCapture();
            photoCaptureHelper.deleteCaptures();
            String fileName = createCaptureFileName();
            Log.e("REGISTER", "start of async task ");
            new EncryptImageAsyncTask(getContext(), photoCaptureHelper, bitmap, fileName, this).execute();
            addPhotoToPhotoKeys(fileName);
        } catch (Exception e) {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG).show();
        }
    }

    private void addPhotoToPhotoKeys(String fileName) throws JSONException {
        if(child.optJSONArray(PHOTO_KEYS) == null){
            JSONArray photo_keys = new JSONArray();
            photo_keys.put(fileName);
            child.put(PHOTO_KEYS, photo_keys);
        }else{
            child.getJSONArray(PHOTO_KEYS).put(fileName);
        }
        child.put(formField.getId(), fileName);
    }

    protected String createCaptureFileName() {
        return UUID.randomUUID().toString();
    }

    protected ImageView getImageView() {
        return new ImageView(getContext());
    }

    protected TextView getImageCaption() {
        return (TextView) findViewById(R.id.caption);
    }

    public void repaint() throws JSONException {
        JSONArray photoKeys = child.optJSONArray(PHOTO_KEYS);
        LinearLayout linearLayout = getGalleryView();
        if(photoKeys != null){
            if (!enabled) {
                for (int i = 0; i < photoKeys.length(); i++) {
                    addImageToView(linearLayout, photoKeys.get(i).toString());
                }
            } else {
                addImageToView(linearLayout, photoKeys.get(photoKeys.length() - 1).toString());
                getImageCaption().setText((getContext() instanceof RegisterChildActivity) ? R.string.photo_capture : R.string.photo_view);
            }
        }
    }

    protected LinearLayout getGalleryView() {
        return (LinearLayout) findViewById(R.id.linear);
    }

    protected void addImageToView(LinearLayout linearLayout, String fileNameWithoutExtension) throws JSONException {
        Bitmap bitmap = photoCaptureHelper.getThumbnailOrDefault(fileNameWithoutExtension);
        ImageView imageView = getImageView();
        imageView.setImageBitmap(bitmap);
        linearLayout.addView(imageView);
    }

}
