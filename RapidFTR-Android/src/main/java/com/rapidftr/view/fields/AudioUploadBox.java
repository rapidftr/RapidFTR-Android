package com.rapidftr.view.fields;

import android.content.Context;
import android.content.res.Resources;
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

import java.io.File;
import java.io.IOException;

public class AudioUploadBox extends BaseView {

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private AudioCaptureHelper audioCaptureHelper;
    Resources resources = RapidFtrApplication.getApplicationInstance().getResources();

    public AudioUploadBox(Context context) {
        super(context);
        audioCaptureHelper = new AudioCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    public AudioUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        audioCaptureHelper = new AudioCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    protected void startRecording(View view) {
        disableButton(findViewById(R.id.start_record), R.drawable.record);
        disableButton(findViewById(R.id.play_record), R.drawable.play);
        enableButton(findViewById(R.id.stop_record), R.drawable.stop_active);
        mRecorder = getMediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.setOutputFile(getNewFileName());
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            new RuntimeException(e);
        }
        mRecorder.start();
    }

    protected void disableButton(View button, int drawable) {
        button.setEnabled(false);
        button.setBackgroundDrawable(resources.getDrawable(drawable));
    }

    protected void enableButton(View button, int drawable) {
        button.setEnabled(true);
        button.setBackgroundDrawable(resources.getDrawable(drawable));
    }

    protected String getNewFileName(){
        String fileName = getFileName();
        deleteFileIfExists(fileName);
        return fileName;
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

    protected void deleteFileIfExists(String fileName) {
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
    }

    protected void stopRecording(View view) {
        disableButton(findViewById(R.id.stop_record), R.drawable.stop);
        enableButton(findViewById(R.id.start_record), R.drawable.record_active);
        enableButton(findViewById(R.id.play_record), R.drawable.play_active);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        child.put(formField.getId(), getFileName());
    }

    protected void playRecording(View view) {
        try {
            final View play = findViewById(R.id.play_record);
            final View record = findViewById(R.id.start_record);
            disableButton(play, R.drawable.play);
            disableButton(record, R.drawable.record);
            mPlayer = getMediaPlayer();
            mPlayer.setDataSource(child.getString(formField.getId()));
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    enableButton(record, R.drawable.record_active);
                    enableButton(play, R.drawable.play_active);
                    mPlayer.release();
                }
            });
        } catch (IOException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            new RuntimeException(e);
        }
    }

    @Override
    protected void initialize() throws JSONException {
        super.initialize();
        if (child.getString(formField.getId()) != null) {
            findViewById(R.id.play_record).setBackgroundDrawable(resources.getDrawable(R.drawable.play_active));
        }
    }

    private void setListeners(boolean canRecord) {
        Button start = (Button) findViewById(R.id.start_record);
        Button stop = (Button) findViewById(R.id.stop_record);
        if (canRecord) {
            start.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    startRecording(view);
                }
            });
            stop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopRecording(view);
                }
            });
        }else {
            start.setBackgroundDrawable(resources.getDrawable(R.drawable.record));
            stop.setBackgroundDrawable(resources.getDrawable(R.drawable.stop));
        }

        findViewById(R.id.play_record).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                playRecording(view);
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setListeners(enabled);
    }

    protected MediaRecorder getMediaRecorder() {
        return new MediaRecorder();
    }

    protected MediaPlayer getMediaPlayer() {
        return new MediaPlayer();
    }
}
