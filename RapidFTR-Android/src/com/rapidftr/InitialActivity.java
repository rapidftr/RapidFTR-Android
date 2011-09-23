package com.rapidftr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InitialActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(InitialActivity.this, CameraPreviewActivity.class);
                startActivity(intent);
            }
        });
    }
}