package com.rapidftr.view.fields;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.model.Child;
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

    @Mock
    MediaRecorder mediaRecorder;

    @Mock
    MediaPlayer mediaPlayer;

    @Before
    public void setUp() {
        initMocks(this);
        view = spy((AudioUploadBox) LayoutInflater.from(new RegisterChildActivity()).inflate(R.layout.form_audio_upload_box, null));
        view.setMediaRecorder(mediaRecorder);
        view.setMediaPlayer(mediaPlayer);
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
        view.startRecording(view);
        verify(mediaRecorder).setAudioSource(MediaRecorder.AudioSource.MIC);
        verify(mediaRecorder).setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        verify(mediaRecorder).setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        verify(mediaRecorder).setOutputFile("audio_file_name");
        verify(mediaRecorder).prepare();
        verify(mediaRecorder).start();
    }

    @Test
    public void shouldStopRecordingWhenStopRecordMethodHasBeenCalled(){
        doReturn("audio_file_name").when(view).getFileName();
        view.initialize(field, child);
        view.stopRecording(view);
        verify(mediaRecorder).stop();
        verify(mediaRecorder).release();
        assertEquals("audio_file_name", child.getString(field.getId()));
    }

    @Test
    public void shouldPlayRecordWhenPlayMethodHasBeenCalled() throws IOException {
        view.initialize(field, child);
        doReturn("audio_file_name").when(view).getFileName();
        view.playRecording(view);
        verify(mediaPlayer).setDataSource(child.getString(field.getId()));
        verify(mediaPlayer).prepare();
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
    public void shouldEnablePlayButtonWhenStopMethodCalled(){
        doReturn("audio_file_name").when(view).getFileName();
        view.initialize(field, child);
        view.stopRecording(view);
        Button play = (Button) view.findViewById(R.id.play_record);
        assertEquals(VISIBLE, play.getVisibility());
    }
}
