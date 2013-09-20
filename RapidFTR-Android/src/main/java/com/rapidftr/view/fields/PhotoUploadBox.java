package com.rapidftr.view.fields;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
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
        repaint();
    }

    private void toggleVisibility() {
        if (enabled) {
            getImageContainer().setVisibility(View.VISIBLE);
            getImageContainer().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClick();
                }
            });
        } else {
            getImageContainer().setVisibility(View.GONE);
        }
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
                    model.put(CURRENT_PHOTO_KEY, data.getStringExtra("file_name"));
                }
                break;
        }
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
        toggleVisibility();
    }

    protected void deleteCapture() {
        if (!model.optBoolean("saved", false)) {
            // TODO: Delete taken image
        }
    }

    public View getImageContainer() {
        return findViewById(R.id.thumbnail);
    }

    public void onImageClick() {
        if (enabled) {
            startCapture();
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
            if(bitmap != null){
            int rotationDegree = photoCaptureHelper.getPictureRotation();
            photoCaptureHelper.deleteCaptures();
            String fileName = createCaptureFileName();
            new EncryptImageAsyncTask(getContext(), photoCaptureHelper, bitmap, fileName, this, rotationDegree).execute();
            addPhotoToPhotoKeys(fileName);
            addCurrentPhotoKeyIfNotPresent(fileName);
            }
        } catch (Exception e) {
            Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG).show();
        }
    }

    private void addCurrentPhotoKeyIfNotPresent(String fileName) {
        if (model.optString(CURRENT_PHOTO_KEY).equals("")) {
            model.put(CURRENT_PHOTO_KEY, fileName);
        }
    }

    private void addPhotoToPhotoKeys(String fileName) throws JSONException {
        if (model.optJSONArray(PHOTO_KEYS) == null) {
            JSONArray photo_keys = new JSONArray();
            photo_keys.put(fileName);
            model.put(PHOTO_KEYS, photo_keys);
        } else {
            model.getJSONArray(PHOTO_KEYS).put(fileName);
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
        final JSONArray photoKeys = model.optJSONArray(PHOTO_KEYS);
        addImageClickListener(photoGridView, photoKeys);
        if (photoKeys != null) {
            setGridAttributes(photoGridView, photoKeys);
            photoGridView.setAdapter(new ImageAdapter(getContext(), model, photoCaptureHelper, enabled));
        }
    }

    protected void setGridAttributes(GridView photoGridView, JSONArray photoKeys) {
        LayoutParams layoutParams = (LayoutParams) photoGridView.getLayoutParams();
        layoutParams.height = measureRealHeightForGridView(photoGridView, photoKeys.length());
    }

    private int measureRealHeightForGridView(GridView gridView, int imagesCount){
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final int screenWidth = windowManager.getDefaultDisplay().getWidth();
        final double screenDensity = getResources().getDisplayMetrics().density;
        final int horizontalSpacing = (int) (2 * screenDensity + 0.5f);
        final int verticalSpacing = (int) (2 * screenDensity + 0.5f);
        final int columnWidth = (int) (90 * screenDensity + 0.5f);
        final int columnsCount = (screenWidth - gridView.getVerticalScrollbarWidth()) / (columnWidth + horizontalSpacing);
        final int rowsCount = imagesCount / columnsCount + (imagesCount % columnsCount == 0 ? 0 : 1);
        return columnWidth * rowsCount + verticalSpacing * (rowsCount - 1);
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

    protected GridView  getGalleryView() {
        return (GridView) findViewById(R.id.photo_grid_view);
    }

}
