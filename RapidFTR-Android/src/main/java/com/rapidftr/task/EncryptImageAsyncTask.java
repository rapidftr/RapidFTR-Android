package com.rapidftr.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.CaptureHelper;
import com.rapidftr.view.fields.PhotoUploadBox;

public class EncryptImageAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    private CaptureHelper captureHelper;
    private Bitmap bitmap;
    private String fileName;
    private PhotoUploadBox photoUploadBox;
    private Context context;
    private int rotationDegree;

    public EncryptImageAsyncTask(Context context, CaptureHelper captureHelper, Bitmap bitmap, String fileName, PhotoUploadBox photoUploadBox, int rotationDegree) {
        this.context = context;
        this.captureHelper = captureHelper;
        this.bitmap = bitmap;
        this.fileName = fileName;
        this.photoUploadBox = photoUploadBox;
        this.rotationDegree = rotationDegree;
    }

    @Override
    protected Boolean doInBackground(Void... bitmaps) {
        try {
            captureHelper.saveThumbnail(bitmap, rotationDegree, fileName);
            captureHelper.savePhoto(bitmap, rotationDegree, fileName);
            return true;
        } catch (Exception e) {
	        return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        try {
	        bitmap.recycle();
	        photoUploadBox.repaint();
        } catch (Exception e) {
	        result = false;
        }

	    if (!result) {
		    Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG);
	    }
    }

}
