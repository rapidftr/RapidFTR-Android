package com.rapidftr.view.fields;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.model.Child;
import com.rapidftr.utils.AudioCaptureHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.IOException;

import static android.view.View.VISIBLE;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class AudioUploadBoxTest extends BaseViewSpec<AudioUploadBox> {

    RapidFtrApplication application;
    AudioCaptureHelper audioCaptureHelper;
    @Mock
    MediaRecorder mediaRecorder;

    @Mock
    MediaPlayer mediaPlayer;

    @Before
    public void setUp() {
        initMocks(this);
        view = spy((AudioUploadBox) LayoutInflater.from(new RegisterChildActivity()).inflate(R.layout.form_audio_upload_box, null));
        application = spy(new RapidFtrApplication());
        audioCaptureHelper = spy(new AudioCaptureHelper(application));
    }

    @Test
    public void shouldCallCorrespondingMethodsWhenButtonsAreClickedAndSetEnabledIsTrue(){
        view.initialize(field, child);
        view.setEnabled(true);
        view.findViewById(R.id.start_record).performClick();
        verify(view).startRecording(Matchers.<View>anyObject());
        view.findViewById(R.id.stop_record).performClick();
        verify(view).stopRecording(Matchers.<View>anyObject());
        view.findViewById(R.id.play_record).performClick();
        verify(view).playRecording(Matchers.<View>anyObject());
    }

    @Test
    public void shouldNotCallCorrespondingMethodsButtonAreClickedAndSetEnabledIsFalse(){
        view.initialize(field, child);
        view.setEnabled(false);
        view.findViewById(R.id.start_record).performClick();
        verify(view, never()).startRecording(Matchers.<View>anyObject());
        view.findViewById(R.id.stop_record).performClick();
        verify(view, never()).stopRecording(Matchers.<View>anyObject());
        view.findViewById(R.id.play_record).performClick();
        verify(view).playRecording(Matchers.<View>anyObject());
    }

    @Test
    public void shouldRecordTheAudioWhenStartRecordMethodHasBeenCalled() throws IOException {
        doReturn("audio_file_name").when(view).getFileName();
        doReturn(mediaRecorder).when(view).getMediaRecorder();
        View start = mock(Button.class);
        View stop = mock(Button.class);
        View play = mock(Button.class);
        doReturn(start).when(view).findViewById(R.id.start_record);
        doReturn(stop).when(view).findViewById(R.id.stop_record);
        doReturn(play).when(view).findViewById(R.id.play_record);
        view.startRecording(view);
        verify(view).disableButton(start, R.drawable.record);
        verify(view).disableButton(play, R.drawable.play);
        verify(view).enableButton(stop, R.drawable.stop_active);
        verify(mediaRecorder).setAudioSource(MediaRecorder.AudioSource.MIC);
        verify(mediaRecorder).setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        verify(mediaRecorder).setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        verify(mediaRecorder).setOutputFile(audioCaptureHelper.getDir().getAbsolutePath()+"/audio_file_name");
        verify(mediaRecorder).prepare();
        verify(mediaRecorder).start();
    }

    @Test
    public void shouldStopRecordingWhenStopRecordMethodHasBeenCalled(){
        view.initialize(field, child);
        doReturn(mediaRecorder).when(view).getMediaRecorder();
        View play = mock(Button.class);
        View record = mock(Button.class);
        View stop = mock(Button.class);
        String fileName = view.getFileName();
        doReturn(play).when(view).findViewById(R.id.play_record);
        doReturn(record).when(view).findViewById(R.id.start_record);
        doReturn(stop).when(view).findViewById(R.id.stop_record);
        doReturn(fileName).when(view).getFileName();
        view.startRecording(view);
        view.stopRecording(view);
        verify(mediaRecorder).stop();
        verify(mediaRecorder).release();
        verify(view).enableButton(play, R.drawable.play_active);
        verify(view).enableButton(record, R.drawable.record_active);
        verify(view).disableButton(stop, R.drawable.stop);
        assertEquals(fileName, child.getString(field.getId()));
    }

    @Test
    public void shouldAddAudioToAttachmentsWhenStopRecordMethodHasBeenCalled() {}

    @Test
    public void shouldPlayRecordWhenPlayMethodHasBeenCalled() throws IOException {
        view.initialize(field, child);
        doReturn("audio_file_name").when(view).getFileName();
        doReturn(mediaPlayer).when(view).getMediaPlayer();
        View record = mock(Button.class);
        View play = mock(Button.class);
        doReturn(record).when(view).findViewById(R.id.start_record);
        doReturn(play).when(view).findViewById(R.id.play_record);

        view.playRecording(view);
        verify(play).setBackgroundDrawable(RapidFtrApplication.getApplicationInstance().getResources().getDrawable(R.drawable.pause_active));
        verify(view).disableButton(record, R.drawable.record);
        verify(mediaPlayer).setDataSource(audioCaptureHelper.getCompleteFileName(child.getString(field.getId())));
        verify(mediaPlayer).prepare();
        verify(mediaPlayer).start();
    }

    @Test
    public void shouldNotIntialiseMediaPlayerWhenItIsInPausedState(){
        view.initialize(field, child);
        doReturn(mediaPlayer).when(view).getMediaPlayer();
        doReturn(false).when(mediaPlayer).isPlaying();
        view.playRecording(view);

        verify(mediaPlayer).start();
    }

    @Test
    public void shouldEnableStopButtonWhenStartRecordMethodCalled(){
        doReturn("audio_file_name").when(view).getFileName();
        view.startRecording(view);
        Button stopButton = (Button)view.findViewById(R.id.stop_record);
        assertEquals(VISIBLE, stopButton.getVisibility());
    }

    @Test
    public void shouldEnablePlayButtonIfThereIsAnAudioFileAvailableForGivenUser(){
        Child givenChild = new Child();
        givenChild.put(field.getId(), "some_audio_file");
        view.initialize(field, givenChild);
        Button play = (Button) view.findViewById(R.id.play_record);
        assertEquals(VISIBLE, play.getVisibility());
    }

    @Test
    public void shouldGiveUniqueFileName(){
        view.initialize(field, child);
        String fileName1 = view.getFileName();
        String fileName2 = view.getFileName();
        assertFalse(fileName1.equals(fileName2));
    }

}
