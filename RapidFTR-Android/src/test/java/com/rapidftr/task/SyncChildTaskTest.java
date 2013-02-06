package com.rapidftr.task;

import android.app.Activity;
import android.content.Intent;
import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class SyncChildTaskTest {

    @Test
    public void shouldRestartActivityOnSuccess(){
        SyncChildTask task = new SyncChildTask(null, null, null);
        Activity activity = mock(Activity.class);
        Intent mockIntent = mock(Intent.class);
        given(activity.getIntent()).willReturn(mockIntent);
        task.setActivity(activity);

        task.onPostExecute(true);

        verify(activity).finish();
        verify(activity).startActivity(mockIntent);
    }
}
