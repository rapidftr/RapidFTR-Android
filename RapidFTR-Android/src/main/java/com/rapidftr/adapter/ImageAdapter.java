package com.rapidftr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.rapidftr.model.Child;
import com.rapidftr.utils.PhotoCaptureHelper;
import org.json.JSONArray;
import org.json.JSONException;

import static com.rapidftr.view.fields.PhotoUploadBox.PHOTO_KEYS;

public class ImageAdapter extends BaseAdapter {

    private final Child child;
    private final boolean enabled;
    private final PhotoCaptureHelper photoCaptureHelper;
    private final Context context;

    public ImageAdapter(Context context, Child child, PhotoCaptureHelper photoCaptureHelper, boolean enabled) {
        this.child = child;
        this.enabled = enabled;
        this.photoCaptureHelper = photoCaptureHelper;
        this.context = context;
    }

    @Override
    public int getCount() {
        return child.optJSONArray(PHOTO_KEYS).length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        JSONArray photoKeys = child.optJSONArray(PHOTO_KEYS);
        ImageView imageView;
        Bitmap bitmap;
        try {
            bitmap = photoCaptureHelper.getThumbnailOrDefault(photoKeys.get(position).toString());
            imageView = new ImageView(context);
            imageView.setPadding(0,0,0,0);
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(bitmap);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return imageView;
    }
}
