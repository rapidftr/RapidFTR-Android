package com.rapidftr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.rapidftr.model.Enquiry;

import java.util.List;

public class EnquiryViewAdapter extends ArrayAdapter<Enquiry> {
    private final Context context;
    private final int textViewResourceId;
    private final List<Enquiry> enquiries;

    public EnquiryViewAdapter(Context context, int textViewResourceId, List<Enquiry> enquiries) {
        super(context, textViewResourceId, enquiries);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.enquiries = enquiries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }
}
