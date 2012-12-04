package com.rapidftr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.rapidftr.utils.CaptureHelper;
import org.json.JSONException;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChildViewAdapter extends ArrayAdapter<Child> {
    private Context context;
    private int textViewResourceId;
    private List<Child> children;
    private CaptureHelper captureHelper;

    public ChildViewAdapter(Context context, int textViewResourceId, List<Child> children) {
        super(context, textViewResourceId, children);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.children = children;
        this.captureHelper = new CaptureHelper(((RapidFtrActivity) context).getContext());
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
                imageView.setImageBitmap(captureHelper.getThumbnailOrDefault(child.optString("current_photo_key")));
                view.setOnClickListener(clickListener(child));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        return view;
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
