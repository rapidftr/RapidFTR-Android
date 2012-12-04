package com.rapidftr.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.utils.CaptureHelper;
import com.rapidftr.view.fields.PhotoUploadBox;
import org.json.JSONException;

import static com.rapidftr.RapidFtrApplication.getApplicationInstance;

public class EncryptImageAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private CaptureHelper captureHelper;
    private Bitmap bitmap;
    private String fileName;
    private PhotoUploadBox photoUploadBox;
    private Context context;

    public EncryptImageAsyncTask(Context context, CaptureHelper captureHelper, Bitmap bitmap, String fileName, PhotoUploadBox photoUploadBox) {
        this.context = context;
        this.captureHelper = captureHelper;
        this.bitmap = bitmap;
        this.fileName = fileName;
        this.photoUploadBox = photoUploadBox;
    }

    @Override
    protected Boolean doInBackground(Void... bitmaps) {
        try {
            captureHelper.saveThumbnail(bitmap, fileName);
            captureHelper.savePhoto(bitmap, fileName);
            return true;
        } catch (Exception e) {
            Toast.makeText(getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        bitmap.recycle();
        try {
            photoUploadBox.repaint();
        } catch (JSONException e) {
            Toast.makeText(getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG);
        }
    }

}
