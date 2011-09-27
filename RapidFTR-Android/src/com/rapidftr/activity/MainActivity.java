package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;

public class MainActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startActivityOn(R.id.login_button, LoginActivity.class);
        startActivityOn(R.id.register_button, RegisterChildActivity.class);
    }
}
