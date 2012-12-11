package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;

public class MainActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityOn(R.id.register_child_button, RegisterChildActivity.class);
        startActivityOn(R.id.search_child, SearchActivity.class);
        startActivityOn(R.id.view_all_children, ViewAllChildrenActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
