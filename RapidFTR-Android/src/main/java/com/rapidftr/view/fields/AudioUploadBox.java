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
import com.rapidftr.activity.BaseChildActivity;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.utils.AudioCaptureHelper;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class AudioUploadBox extends BaseView {

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String fileName;
    private BaseChildActivity context;

    private AudioCaptureHelper audioCaptureHelper;
    Resources resources = RapidFtrApplication.getApplicationInstance().getResources();

    public AudioUploadBox(Context context) {
        super(context);
        this.context = (BaseChildActivity) context;
        audioCaptureHelper = getHelper(context);
    }

    public AudioUploadBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (BaseChildActivity) context;
        audioCaptureHelper = getHelper(context);
    }

    protected AudioCaptureHelper getHelper(Context context) {
        return new AudioCaptureHelper(((RapidFtrActivity) context).getContext());
    }

    protected void startRecording(final View view) {
        disableButton(findViewById(R.id.start_record), R.drawable.record);
        disableButton(findViewById(R.id.play_record), R.drawable.play);
        enableButton(findViewById(R.id.stop_record), R.drawable.stop_active);
        mRecorder = getMediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setMaxDuration(60000);
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording(view);
                }
            }
        });
        try {
            mRecorder.setOutputFile(audioCaptureHelper.getDir().getAbsolutePath() + "/"+ getFileName());
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            throw  new RuntimeException(e);
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

    protected String getFileName() {
        try {
            String newFileName = null;
            while(newFileName == null || (fileName !=null && fileName.equals(newFileName))){
               newFileName = (model.getId() == null? "" : model.getId()) + new Date().getTime();
            }
            fileName = newFileName;
        } catch (JSONException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
            new RuntimeException(e);
        }
        return fileName;
    }

    protected void stopRecording(View view) {
        disableButton(findViewById(R.id.stop_record), R.drawable.stop);
        enableButton(findViewById(R.id.start_record), R.drawable.record_active);
        enableButton(findViewById(R.id.play_record), R.drawable.play_active);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        model.put(formField.getId(), fileName);
    }

    protected void playRecording(View view) {
        try {
            final View play = findViewById(R.id.play_record);
            final View record = findViewById(R.id.start_record);

	        try {
	            if(mPlayer != null && mPlayer.isPlaying()){
	                play.setBackgroundDrawable(resources.getDrawable(R.drawable.play_active));
	                mPlayer.pause();
	                return;
	            } else if(mPlayer != null){
	                play.setBackgroundDrawable(resources.getDrawable(R.drawable.pause_active));
	                mPlayer.start();
	                return;
	            }
	        } catch (Exception e) { }

            disableButton(record, R.drawable.record);
            play.setBackgroundDrawable(resources.getDrawable(R.drawable.pause_active));

	        mPlayer = null;
            mPlayer = getMediaPlayer();
            mPlayer.setDataSource(audioCaptureHelper.getCompleteFileName(model.getString(formField.getId())));
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    enableButton(record, R.drawable.record_active);
                    enableButton(play, R.drawable.play_active);
                    mPlayer.release();
                    mPlayer = null;
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
        if (model.getString(formField.getId()) != null) {
            enableButton(findViewById(R.id.play_record), R.drawable.play_active);
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
            disableButton(start, R.drawable.record);
            disableButton(stop, R.drawable.stop);
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
        MediaRecorder mediaRecorder = new MediaRecorder();
        context.setMediaRecorder(mediaRecorder);
        return mediaRecorder;
    }

    protected MediaPlayer getMediaPlayer() {
        if(mPlayer == null)
            mPlayer = new MediaPlayer();
        context.setMediaPlayer(mPlayer);
        return mPlayer;
    }
}
