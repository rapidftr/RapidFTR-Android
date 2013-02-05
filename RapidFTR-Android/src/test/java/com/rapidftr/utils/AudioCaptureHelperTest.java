package com.rapidftr.utils;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.String;

import static org.junit.Assert.*;
import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;

@RunWith(CustomTestRunner.class)
public class AudioCaptureHelperTest {
    AudioCaptureHelper audioCaptureHelper;

    @Before
    public void setup(){
      audioCaptureHelper = new AudioCaptureHelper(mockContext());
    }

    @Test
    public void shouldSaveAudio() throws Exception {
        Child child = new Child("id1", "user1", "{ 'recorded_audio' : 'some_audio_file_name' }");
        String data = "some audio stream 10101010";
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        audioCaptureHelper.saveAudio(child, inputStream);
        assertTrue((new File(audioCaptureHelper.getDir(), "some_audio_file_name")).exists());
    }

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }

}
