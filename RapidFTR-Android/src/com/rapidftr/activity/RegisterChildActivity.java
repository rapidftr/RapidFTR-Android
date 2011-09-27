package com.rapidftr.activity;

import android.os.Bundle;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.ChildDetailsForm;
import org.codehaus.jackson.map.ObjectMapper;

public class RegisterChildActivity extends RapidFtrActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_child);
        try{
            ChildDetailsForm[] formSections = RapidFtrApplication.getChildFormSections();
            loge(String.valueOf(formSections.length));
        }
        catch (Exception ex){
            String str = ex.getMessage();
        }
    }
}

