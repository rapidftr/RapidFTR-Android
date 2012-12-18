package com.rapidftr.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.rapidftr.utils.CaptureHelper;

public class ChildrenListThumbnailAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private CaptureHelper captureHelper;
    private String imageName;

    public ChildrenListThumbnailAsyncTask(ImageView imageView, CaptureHelper captureHelper) {
        this.imageView = imageView;
        this.captureHelper = captureHelper;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        imageName = params[0];
        return captureHelper.getThumbnailOrDefault(imageName);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
