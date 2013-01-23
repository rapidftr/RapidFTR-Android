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

import java.io.IOException;

public class AudioUploadBox extends BaseView {

    private MediaRecorder mRecorder = new MediaRecorder();
    private MediaPlayer mPlayer = new MediaPlayer();

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
        ((Button) findViewById(R.id.start_record)).setBackgroundDrawable(resources.getDrawable(R.drawable.record));
        ((Button) findViewById(R.id.stop_record)).setBackgroundDrawable(resources.getDrawable(R.drawable.stop_active));
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
        findViewById(R.id.stop_record).setBackgroundDrawable(resources.getDrawable(R.drawable.stop));
        mRecorder.stop();
        mRecorder.release();
        findViewById(R.id.start_record).setBackgroundDrawable(resources.getDrawable(R.drawable.record_active));
        findViewById(R.id.play_record).setBackgroundDrawable(resources.getDrawable(R.drawable.play_active));
        child.put(formField.getId(), getFileName());
    }

    protected void playRecording(View view) {
        try {
            final Button play = (Button)findViewById(R.id.play_record);
            play.setBackgroundDrawable(resources.getDrawable(R.drawable.play));
            mPlayer.setDataSource(child.getString(formField.getId()));
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    play.setBackgroundDrawable(resources.getDrawable(R.drawable.play_active));
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

    protected void setMediaRecorder(MediaRecorder mediaRecorder) {
        mRecorder = mediaRecorder;
    }

    protected void setMediaPlayer(MediaPlayer mediaPlayer) {
        mPlayer = mediaPlayer;
    }
}
