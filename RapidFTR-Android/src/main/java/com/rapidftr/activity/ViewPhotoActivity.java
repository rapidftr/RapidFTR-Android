package com.rapidftr.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.utils.CaptureHelper;

public class ViewPhotoActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        this.initialize();
    }

    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.photo);
    }

    public void initialize() {
        CaptureHelper helper = new CaptureHelper();
        String fileName = getIntent().getStringExtra("file_name");

        try {
            getImageView().setImageBitmap(helper.load(fileName));
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.view_photo_error, Toast.LENGTH_LONG).show();
        }
    }

}
