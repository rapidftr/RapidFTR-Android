package com.rapidftr.utils;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import org.json.JSONException;

import java.io.*;

public class AudioCaptureHelper extends CaptureHelper{

    public AudioCaptureHelper(RapidFtrApplication context) {
        super(context);
    }

    public void saveAudio(Child child, InputStream inputStream) throws JSONException, IOException {
        File file = new File(getDir(), child.optString("recorded_audio"));
        if (!file.exists() && !child.optString("recorded_audio").equals("")){
            IOUtils.copy(inputStream, new FileOutputStream(file));
        }
    }
    
    public String getCompleteFileName(String fileName){
        return getDir().getAbsolutePath() + "/"+ fileName;
    }
    
}
