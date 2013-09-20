package com.rapidftr.activity;

import android.view.View;
import com.rapidftr.R;

public class CreateEnquiryActivity extends BaseEnquiryActivity {
    public void initializeView() {
        setContentView(R.layout.activity_create_enquiry);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }
}
