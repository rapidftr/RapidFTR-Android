package com.rapidftr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.CollectionActivity;
import com.rapidftr.forms.FormField;
import com.rapidftr.model.BaseModel;
import com.rapidftr.service.FormService;
import com.rapidftr.view.HighlightedFieldViewGroup;
import org.json.JSONException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HighlightedFieldsViewAdapter<T extends BaseModel> extends BaseModelViewAdapter<T> {

    protected Map<Integer, FormField> highlightedFields;
    private FormService formService;
    private Class<CollectionActivity> activityToLaunch;

    public HighlightedFieldsViewAdapter(Context context, List<T> baseModels, String formName, Class<CollectionActivity> activityToLaunch) {
        super(context, R.layout.row_highlighted_fields, baseModels);

        formService = RapidFtrApplication.getApplicationInstance().getBean(FormService.class);

        List<FormField> fields = formService.getHighlightedFields(formName);

        highlightedFields = new TreeMap<Integer, FormField>();
        this.activityToLaunch = activityToLaunch;

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
        final BaseModel baseModel = objects.get(position);
        if (baseModel != null) {
            TextView uniqueIdView = (TextView) view.findViewById(R.id.row_child_unique_id);

            HighlightedFieldViewGroup highlightedFieldViewGroup = (HighlightedFieldViewGroup) view.findViewById(R.id.child_field_group);
            highlightedFieldViewGroup.prepare(baseModel, highlightedFields);

            ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
            try {
                setFields(String.valueOf(baseModel.getShortId()), uniqueIdView);
                assignThumbnail(baseModel, imageView);

                view.setOnClickListener(createClickListener(baseModel, activityToLaunch));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return view;
    }
}
