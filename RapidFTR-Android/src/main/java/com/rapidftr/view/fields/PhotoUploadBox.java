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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.activity.ViewPhotoActivity;
import com.rapidftr.adapter.ImageAdapter;
import com.rapidftr.task.EncryptImageAsyncTask;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;

import static com.rapidftr.activity.BaseChildActivity.CLOSE_ACTIVITY;

public class PhotoUploadBox extends BaseView implements RapidFtrActivity.ResultListener {

    public static final int CAPTURE_IMAGE_REQUEST = 100;
    public static final int SHOW_FULL_IMAGE_REQUEST = 200;
    public static final String PHOTO_KEYS = "photo_keys";
    public static final String CURRENT_PHOTO_KEY = "current_photo_key";

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
        activity.addResultListener(SHOW_FULL_IMAGE_REQUEST, this);

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
            case SHOW_FULL_IMAGE_REQUEST:
                if (data != null && data.getStringExtra("file_name") != null) {
                    child.put(CURRENT_PHOTO_KEY, data.getStringExtra("file_name"));
                }
                break;
        }
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
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
            showFullPhoto(null);
        }
    }

    protected void showFullPhoto(String fileName) {
        Activity context = (Activity) getContext();
        try {
            if (fileName == null) {
                Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_not_captured, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context, ViewPhotoActivity.class);
                intent.putExtra("file_name", fileName);
                intent.putExtra("enabled", enabled);
                context.startActivityForResult(intent, SHOW_FULL_IMAGE_REQUEST);
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
            int rotationDegree = photoCaptureHelper.getPictureRotation();
            photoCaptureHelper.deleteCaptures();
            String fileName = createCaptureFileName();
            Log.e("REGISTER", "start of async task ");
            new EncryptImageAsyncTask(getContext(), photoCaptureHelper, bitmap, fileName, this, rotationDegree).execute();
            addPhotoToPhotoKeys(fileName);
            addCurrentPhotoKeyIfNotPresent(fileName);
        } catch (Exception e) {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG).show();
        }
    }

    private void addCurrentPhotoKeyIfNotPresent(String fileName) {
        if (child.optString(CURRENT_PHOTO_KEY).equals("")) {
            child.put(CURRENT_PHOTO_KEY, fileName);
        }
    }

    private void addPhotoToPhotoKeys(String fileName) throws JSONException {
        if (child.optJSONArray(PHOTO_KEYS) == null) {
            JSONArray photo_keys = new JSONArray();
            photo_keys.put(fileName);
            child.put(PHOTO_KEYS, photo_keys);
        } else {
            child.getJSONArray(PHOTO_KEYS).put(fileName);
        }
    }

    protected String createCaptureFileName() {
        return UUID.randomUUID().toString();
    }

    protected ImageView getImageView() {
        return new ImageView(getContext());
    }

    public void repaint() throws JSONException {
        GridView photoGridView = getGalleryView();
        final JSONArray photoKeys = child.optJSONArray(PHOTO_KEYS);
        addImageClickListener(photoGridView, photoKeys);
        if (photoKeys != null) {
            photoGridView.setAdapter(new ImageAdapter(getContext(), child, photoCaptureHelper, enabled));
        }
    }

    private void addImageClickListener(GridView photoGridView, final JSONArray photoKeys) {
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (photoKeys != null) {
                    try {
                        showFullPhoto(photoKeys.get(position).toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    protected GridView getGalleryView() {
        return (GridView) findViewById(R.id.photo_grid_view);
    }

}
