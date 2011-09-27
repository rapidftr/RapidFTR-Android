package com.rapidftr.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public abstract class RapidFtrActivity extends Activity {

    public static final String APP_IDENTIFIER = "RapidFTR";

    protected void startActivityOn(int viewResId, final Class<? extends RapidFtrActivity> activityClass) {
        findViewById(viewResId).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(RapidFtrActivity.this, activityClass));
            }
        });
    }

    protected void loge(String message) {
        Log.e(APP_IDENTIFIER, message);
    }

    protected void logd(String message) {
        Log.d(APP_IDENTIFIER, message);
    }

    protected void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
