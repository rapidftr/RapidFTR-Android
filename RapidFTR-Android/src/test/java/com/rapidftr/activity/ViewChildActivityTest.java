package com.rapidftr.activity;

import android.content.Intent;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.task.SyncChildTask;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.io.SyncFailedException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ViewChildActivityTest {

    @Mock
    User currentUser;

    protected ViewChildActivity activity;

    @Before
    public void setUp() {
        activity = spy(new ViewChildActivity());
        Robolectric.shadowOf(activity).setIntent(new Intent().putExtra("id", "id1"));
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
        SyncChildTask task = mock(SyncChildTask.class);

        doReturn(task).when(activity).inject(SyncChildTask.class);

        activity.child = child;
        activity.sync();

        verify(task).setActivity(activity);
        verify(task).doInBackground(child);
    }

    @Test
    public void shouldCallSyncWhenMenuSelected() {
        doNothing().when(activity).sync();
        MenuItem item = mock(MenuItem.class);
        given(item.getItemId()).willReturn(R.id.synchronize_child);
        activity.onOptionsItemSelected(item);
        verify(activity).sync();
    }

    @Test
    public void shouldCallGetServerURLWhenMenuSelected() throws Exception {
        doNothing().when(activity).getServerAndSync();
        User currUser = ((RapidFtrApplication) Robolectric.application).getCurrentUser();
        currUser.setServerUrl(null);
        MenuItem item = mock(MenuItem.class);
        given(item.getItemId()).willReturn(R.id.synchronize_child);
        activity.onOptionsItemSelected(item);
        verify(activity).getServerAndSync();
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
