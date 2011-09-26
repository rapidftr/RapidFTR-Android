package com.rapidftr.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import com.rapidftr.R;
import com.rapidftr.view.Preview;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraPreviewActivity extends Activity {
    private static final String TAG = "CameraDemo";

    private Preview preview;
    private Button buttonClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);

        buttonClick = (Button) findViewById(R.id.buttonClick);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                preview.getCamera().takePicture(null, null, jpegCallback);
            }
        });

        Log.d(TAG, "onCreate'd");
    }

    @Override
    protected void onResume() {
        super.onResume();
        preview.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.releaseCamera();
    }

	Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				outStream = new FileOutputStream(String.format(
						"/sdcard/%d.jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
}