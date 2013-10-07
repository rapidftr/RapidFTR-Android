package com.rapidftr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.activity.ViewEnquiryActivity;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EnquiryViewAdapter extends BaseModelViewAdapter<Enquiry> {

    public EnquiryViewAdapter(Context context, int textViewResourceId, List<Enquiry> enquiries) {
        super(context, textViewResourceId, enquiries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(textViewResourceId, null);
        }
        final Enquiry enquiry = objects.get(position);
        if (enquiry != null) {
            TextView nameView = (TextView) view.findViewById(R.id.row_enquiry_enquirer_name);
            TextView dateView = (TextView) view.findViewById(R.id.row_enquiry_creation_date);

            try {
                setFields(enquiry.getCreatedAt(), dateView);
                setFields(enquiry.getEnquirerName(), nameView);
                view.setOnClickListener(createClickListener(enquiry, ViewEnquiryActivity.class));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return view;
    }

}
