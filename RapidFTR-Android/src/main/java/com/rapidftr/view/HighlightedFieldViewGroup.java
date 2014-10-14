package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormField;
import com.rapidftr.model.BaseModel;
import com.rapidftr.utils.StringUtils;

import java.util.Iterator;
import java.util.Map;

public class HighlightedFieldViewGroup extends LinearLayout {
    public HighlightedFieldViewGroup(Context context) {
        super(context);
    }

    public HighlightedFieldViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HighlightedFieldViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void prepare(BaseModel baseModel, Map<Integer, FormField> highlightedFields) {
        Iterator<Integer> iterator = highlightedFields.keySet().iterator();
        while (iterator.hasNext()) {
            Integer fieldId = iterator.next();
            TextView textView = (TextView) this.findViewById(fieldId);
            if (textView == null) {
                textView = new TextView(getContext());
                textView.setId(fieldId);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                textView.setPadding(5, 0, 0, 2);

                this.addView(textView);
            }

            if (StringUtils.isNotEmpty(baseModel.optString(highlightedFields.get(fieldId).getId()))) {
                String fieldValue = String.format("%s: %s",
                        highlightedFields.get(fieldId).getDisplayName().get(RapidFtrApplication.getApplicationInstance().getLanguageOfCurrentUser()),
                        baseModel.optString(highlightedFields.get(fieldId).getId()));

                textView.setText(fieldValue);
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

    }
}
