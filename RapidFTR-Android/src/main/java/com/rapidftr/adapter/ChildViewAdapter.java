package com.rapidftr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.activity.ViewChildActivity;
import com.rapidftr.model.Child;
import com.rapidftr.task.AssignThumbnailAsyncTask;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.json.JSONException;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChildViewAdapter extends ArrayAdapter<Child> {
    private Context context;
    private int textViewResourceId;
    private List<Child> children;
    private PhotoCaptureHelper photoCaptureHelper;

    public ChildViewAdapter(Context context, int textViewResourceId, List<Child> children) {
        super(context, textViewResourceId, children);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.children = children;
        this.photoCaptureHelper = new PhotoCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(textViewResourceId, null);
        }
        final Child child = children.get(position);
        if (child != null) {
            TextView uniqueIdView = (TextView) view.findViewById(R.id.row_child_unique_id);
            TextView nameView = (TextView) view.findViewById(R.id.row_child_name);
            ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
            try {
                setFields(String.valueOf(child.getShortId()), uniqueIdView);
                setFields(String.valueOf(child.optString("name")), nameView);
                assignThumbnail(child, imageView);

                view.setOnClickListener(clickListener(child));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return view;
    }

    private void assignThumbnail(Child child, ImageView imageView) {
        String current_photo_key = child.optString("current_photo_key");
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
                ThumbnailDrawable thumbnailDrawable = (ThumbnailDrawable)drawable;
                return thumbnailDrawable.getAssignThumbnailAsyncTask();
            }
        }
        return null;
    }

    private View.OnClickListener clickListener(final Child child) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewChildActivity.class);
                try {
                    intent.putExtra("id", child.getUniqueId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Activity activity = (Activity) context;
                activity.finish();
                activity.startActivity(intent);
            }
        };
    }

    private void setFields(String text, TextView textView) {
        if (textView != null) {
            textView.setText(text);
        }
        
    }

}
