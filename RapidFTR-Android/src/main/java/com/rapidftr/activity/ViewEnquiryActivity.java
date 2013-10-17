package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.rapidftr.R;
import org.json.JSONException;

import java.io.IOException;

public class ViewEnquiryActivity extends BaseEnquiryActivity{

    @Override
    protected void initializeView() {
        Log.e("Check","Setting the content view" );
        setContentView(R.layout.activity_view_enquiry);
        Log.e("Check", "Setting the content view DONE");
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(enquiry.getShortId());
    }

    public void edit(View view) throws JSONException {
        Intent editEnquiryIntent = new Intent(this, EditEnquiryActivity.class);
        editEnquiryIntent.putExtra("id", enquiry.getUniqueId());
        startActivity(editEnquiryIntent);
    }
}
