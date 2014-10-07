package com.rapidftr.utils;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import org.json.JSONException;

import java.io.*;

public class AudioCaptureHelper extends CaptureHelper{

    public AudioCaptureHelper(RapidFtrApplication context) {
        super(context);
    }

    public void saveAudio(BaseModel baseModel, InputStream inputStream) throws JSONException, IOException {
        File file = new File(getDir(), baseModel.optString("recorded_audio"));
        if (!file.exists() && !baseModel.optString("recorded_audio").equals("")){
            IOUtils.copy(inputStream, new FileOutputStream(file));
        }
    }
    
    public String getCompleteFileName(String fileName){
        return getDir().getAbsolutePath() + "/"+ fileName;
    }
    
}
