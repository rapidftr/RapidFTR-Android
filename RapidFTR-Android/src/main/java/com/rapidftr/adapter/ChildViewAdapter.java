package com.rapidftr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.activity.ViewChildActivity;
import com.rapidftr.model.Child;
import org.json.JSONException;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChildViewAdapter extends BaseModelViewAdapter<Child> {

    public ChildViewAdapter(Context context, int textViewResourceId, List<Child> children) {
        super(context, textViewResourceId, children);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(textViewResourceId, null);
        }
        final Child child = objects.get(position);
        if (child != null) {
            TextView uniqueIdView = (TextView) view.findViewById(R.id.row_child_unique_id);
            TextView nameView = (TextView) view.findViewById(R.id.row_child_name);
            ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
            try {
                setFields(String.valueOf(child.getShortId()), uniqueIdView);
                setFields(String.valueOf(child.optString("name")), nameView);
                assignThumbnail(child, imageView);

                view.setOnClickListener(createClickListener(child, ViewChildActivity.class));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return view;
    }


}
