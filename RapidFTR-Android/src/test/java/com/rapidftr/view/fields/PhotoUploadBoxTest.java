package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(CustomTestRunner.class)
public class PhotoUploadBoxTest extends BaseViewSpec<PhotoUploadBox> {

    @Before
    public void setUp() {
        view = (PhotoUploadBox) LayoutInflater.from(new Activity()).inflate(R.layout.form_photo_upload_box, null);
    }

    // Tests from BaseViewSpec are run

}
