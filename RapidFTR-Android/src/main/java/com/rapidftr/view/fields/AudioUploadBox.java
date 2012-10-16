package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import com.rapidftr.R;

public class AudioUploadBox extends BaseView {

    public static final int LAYOUT_RESOURCE_ID = R.layout.form_audio_upload_box;

    public AudioUploadBox(Context context) {
        super(context);
    }

    public AudioUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() {
        // Nothing to do for now
    }

}
