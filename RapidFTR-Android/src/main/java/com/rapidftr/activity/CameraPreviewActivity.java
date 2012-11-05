package com.rapidftr.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.rapidftr.R;
import com.rapidftr.view.CameraPreview;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraPreviewActivity extends RapidFtrActivity {
    private CameraPreview cameraPreview;
    private Button buttonClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraPreview = new CameraPreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(cameraPreview);

        buttonClick = (Button) findViewById(R.id.buttonClick);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cameraPreview.getCamera().takePicture(null, null, jpegCallback);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreview.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.releaseCamera();
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
        }
    };
}