package com.rapidftr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.ViewChildActivity;
import com.rapidftr.forms.FormField;
import com.rapidftr.model.Child;
import com.rapidftr.service.FormService;
import com.rapidftr.view.ChildHighlightedFieldViewGroup;
import org.json.JSONException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChildViewAdapter extends BaseModelViewAdapter<Child> {

    protected Map<Integer, FormField> highlightedFields;

    private FormService formService;

    public ChildViewAdapter(Context context, int textViewResourceId, List<Child> children) {
        super(context, textViewResourceId, children);

        formService = RapidFtrApplication.getApplicationInstance().getBean(FormService.class);

        List<FormField> fields = formService.getHighlightedFields(Child.CHILD_FORM_NAME);

        highlightedFields = new TreeMap<Integer, FormField>();

        int counter = 0;
        for (FormField formField : fields) {
            int id = ++counter;
            highlightedFields.put(id, formField);
        }
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

            ChildHighlightedFieldViewGroup childHighlightedFieldViewGroup = (ChildHighlightedFieldViewGroup) view.findViewById(R.id.child_field_group);
            childHighlightedFieldViewGroup.prepare(child, highlightedFields);

            ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
            try {
                setFields(String.valueOf(child.getShortId()), uniqueIdView);
                assignThumbnail(child, imageView);

                view.setOnClickListener(createClickListener(child, ViewChildActivity.class));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return view;
    }
}
