package com.rapidftr.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.view.fields.PhotoUploadBox;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;

public class EncryptImageAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    private PhotoCaptureHelper photoCaptureHelper;
    private Bitmap bitmap;
    private String fileName;
    private PhotoUploadBox photoUploadBox;
    private Context context;

    public EncryptImageAsyncTask(Context context, PhotoCaptureHelper photoCaptureHelper, Bitmap bitmap, String fileName, PhotoUploadBox photoUploadBox) {
        this.context = context;
        this.photoCaptureHelper = photoCaptureHelper;
        this.bitmap = bitmap;
        this.fileName = fileName;
        this.photoUploadBox = photoUploadBox;
    }

    @Override
    protected Boolean doInBackground(Void... bitmaps) {
        try {
            photoCaptureHelper.saveThumbnail(bitmap, fileName);
            photoCaptureHelper.savePhoto(bitmap, fileName);
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
	        Log.e(APP_IDENTIFIER, "Error saving photo", e);
	        result = false;
        }

	    if (!result) {
		    Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_capture_error, Toast.LENGTH_LONG).show();
	    }
    }

}
