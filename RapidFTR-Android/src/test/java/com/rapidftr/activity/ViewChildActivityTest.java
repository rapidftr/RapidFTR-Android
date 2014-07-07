package com.rapidftr.activity;

import android.content.Intent;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.task.SyncSingleRecordTask;
import com.rapidftr.utils.SpyActivityController;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ViewChildActivityTest {

    private SpyActivityController<ViewChildActivity> activityController;
    protected ViewChildActivity activity;

    @Before
    public void setUp() throws JSONException {
        activityController = SpyActivityController.of(ViewChildActivity.class);
        activity = activityController.attach().get();
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorIfChildIsNotFound() throws Exception {
        activity.initializeData(null);
    }

    @Test
    public void testEditListener() throws JSONException {
        doNothing().when(activity).edit();

        activity.initializeView();

        activity.findViewById(R.id.submit).performClick();
        verify(activity).edit();
    }

    @Test
    public void shouldSyncAndShowChildRecord() throws IOException, JSONException {
        Child child = mock(Child.class);
        SyncSingleRecordTask syncRecordTask = mock(SyncSingleRecordTask.class);
        doReturn(syncRecordTask).when(activity).createChildSyncTask();

        activity.child = child;
        activity.sync();
        verify(syncRecordTask).setActivity(activity);
        verify(syncRecordTask).doInBackground(child);
    }

    @Test
    public void shouldCallSyncWhenMenuSelected() {
        doNothing().when(activity).sync();
        MenuItem item = mock(MenuItem.class);
        given(item.getItemId()).willReturn(R.id.sync_single);
        activity.onOptionsItemSelected(item);
        verify(activity).sync();
    }

    @Test
    public void shouldShowSyncLogMenuItemAfterSyncFailure(){
        Child child = mock(Child.class);
        when(child.isSynced()).thenReturn(false);

        doNothing().when(activity).showSyncLog();
        MenuItem item = mock(MenuItem.class);
        given(item.getItemId()).willReturn(R.id.synchronize_log);
        activity.onOptionsItemSelected(item);
        verify(activity).showSyncLog();

    }

    @Test
    public void shouldShowSyncErrorLogIfSyncFailed() throws Exception {
        String syncError = "Actual Sync Error";
        Child child = mock(Child.class);
        when(child.getSyncLog()).thenReturn(syncError);
        activity.child = child;
        activity.showSyncLog();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo("Error occurred while syncing the record with the server, please try again."));
    }

}
