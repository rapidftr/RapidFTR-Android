package com.rapidftr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rapidftr.activity.CollectionActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.BaseModel;
import com.rapidftr.task.AssignThumbnailAsyncTask;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.json.JSONException;

import java.util.List;

public class BaseModelViewAdapter<T> extends ArrayAdapter<T> {
    protected final Context context;
    protected final int textViewResourceId;
    protected List<T> objects;
    protected PhotoCaptureHelper photoCaptureHelper;

    public BaseModelViewAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
        this.photoCaptureHelper = new PhotoCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    protected void setFields(String text, TextView textView) {
        if (textView != null) {
            textView.setText(text);
        }

    }

    protected void assignThumbnail(BaseModel model, ImageView imageView) {
        String current_photo_key = model.optString("current_photo_key");
        if (cancelPotentialDownload(current_photo_key, imageView)) {
            AssignThumbnailAsyncTask task = new AssignThumbnailAsyncTask(imageView, photoCaptureHelper);
            ThumbnailDrawable drawable = new ThumbnailDrawable(task);
            imageView.setImageDrawable(drawable);
            task.execute(current_photo_key);
        }
    }

    private boolean cancelPotentialDownload(String current_photo_key, ImageView imageView) {
        AssignThumbnailAsyncTask bitmapDownloaderTask = getAssignThumbnailAsyncTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapFilename = bitmapDownloaderTask.getImageName();
            if ((bitmapFilename == null) || (!bitmapFilename.equals(current_photo_key))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private AssignThumbnailAsyncTask getAssignThumbnailAsyncTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof ThumbnailDrawable) {
                ThumbnailDrawable thumbnailDrawable = (ThumbnailDrawable) drawable;
                return thumbnailDrawable.getAssignThumbnailAsyncTask();
            }
        }
        return null;
    }

    protected View.OnClickListener createClickListener(final BaseModel object, final Class<? extends CollectionActivity> activityToLaunch) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityToLaunch);
                try {
                    intent.putExtra("id", object.getUniqueId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Activity activity = (Activity) context;
                activity.finish();
                activity.startActivity(intent);
            }
        };
    }
}
