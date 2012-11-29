package com.rapidftr.activity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.utils.CaptureHelper;

public class ViewPhotoActivity extends RapidFtrActivity {

    protected CaptureHelper captureHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        captureHelper = new CaptureHelper(getContext());
        this.initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.photo);
    }

    public void initialize() {
        setContentView(R.layout.activity_view_photo);
        String fileName = getIntent().getStringExtra("file_name");

        try {
            getImageView().setImageBitmap(captureHelper.loadPhoto(fileName));
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.photo_view_error, Toast.LENGTH_LONG).show();
        }
    }

}
