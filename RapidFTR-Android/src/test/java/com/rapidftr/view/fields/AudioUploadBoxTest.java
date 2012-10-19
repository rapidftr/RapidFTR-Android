package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.forms.FormField;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(CustomTestRunner.class)
public class AudioUploadBoxTest extends BaseViewSpec<AudioUploadBox> {

    @Before
    public void setUp() {
        view = (AudioUploadBox) LayoutInflater.from(new Activity()).inflate(R.layout.form_audio_upload_box, null);
    }

    // Tests from BaseViewSpec are run

}
