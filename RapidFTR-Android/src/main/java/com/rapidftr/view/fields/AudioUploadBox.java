package com.rapidftr.view.fields;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.utils.AudioCaptureHelper;
import org.json.JSONException;

import java.io.IOException;

public class AudioUploadBox extends BaseView {

    private MediaRecorder mRecorder = new MediaRecorder();
    private MediaPlayer mPlayer = new MediaPlayer();

    private AudioCaptureHelper audioCaptureHelper;

    public AudioUploadBox(Context context) {
        super(context);
        audioCaptureHelper = new AudioCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    public AudioUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        audioCaptureHelper = new AudioCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    protected void startRecording(View view) {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.setOutputFile(getFileName());
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            new RuntimeException(e);
        }
        ((Button)findViewById(R.id.stop_record)).setVisibility(VISIBLE);
        mRecorder.start();
    }

    protected String getFileName() {
        String fileName = null;
        try {
            fileName = audioCaptureHelper.getDir().getAbsolutePath() + child.getUniqueId() + formField.getId();
        } catch (JSONException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            new RuntimeException(e);
        }
        return fileName;
    }

    protected void stopRecording(View view) {
        mRecorder.stop();
        mRecorder.release();
        findViewById(R.id.play_record).setVisibility(VISIBLE);
        child.setAudio(formField.getId(), getFileName());
        mRecorder = null;
    }

    protected void playRecording(View view) {
        try {
            mPlayer.setDataSource(child.getAudio(formField.getId()));
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            new RuntimeException(e);
        }
    }

    @Override
    protected void initialize() throws JSONException {
        super.initialize();
        Button start = (Button)findViewById(R.id.start_record);
        Button stop = (Button)findViewById(R.id.stop_record);
        Button play = (Button)findViewById(R.id.play_record);

        if(child.getAudio(formField.getId()) != null){
            play.setVisibility(VISIBLE);
        }

        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording(view);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording(view);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRecording(view);
            }
        });


    }

    protected void setMediaRecorder(MediaRecorder mediaRecorder){
        mRecorder = mediaRecorder;
    }

    protected void setMediaPlayer(MediaPlayer mediaPlayer){
        mPlayer = mediaPlayer;
    }


}
