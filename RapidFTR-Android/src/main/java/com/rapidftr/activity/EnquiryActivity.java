package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.repository.EnquiryRepository;


public class EnquiryActivity extends RapidFtrActivity{

    EnquiryRepository repository = inject(EnquiryRepository.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.enquiry_list);
        TextView enquiryList = (TextView) findViewById(R.id.enquiryList);
        enquiryList.setText(repository.getAllEnquirerNames().toString());
    }

   protected void registerEnquiryActivity() {
       new Intent(this, RegisterEnquiryActivity.class);
   }
}
