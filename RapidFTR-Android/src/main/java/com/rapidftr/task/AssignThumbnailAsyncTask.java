package com.rapidftr.task;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.rapidftr.adapter.ThumbnailDrawable;
import com.rapidftr.utils.CaptureHelper;

public class AssignThumbnailAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private CaptureHelper captureHelper;
    private String imageName;

    public AssignThumbnailAsyncTask(ImageView imageView, CaptureHelper captureHelper) {
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
        AssignThumbnailAsyncTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
        // Change bitmap only if this process is still associated with it
        if (this == bitmapDownloaderTask) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public String getImageName() {
        return imageName;
    }

    private static AssignThumbnailAsyncTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof ThumbnailDrawable) {
                ThumbnailDrawable downloadedDrawable = (ThumbnailDrawable)drawable;
                return downloadedDrawable.getAssignThumbnailAsyncTask();
            }
        }
        return null;
    }

}
