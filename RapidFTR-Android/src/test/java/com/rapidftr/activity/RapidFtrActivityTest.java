package com.rapidftr.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SynchronisationAsyncTask;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.net.ConnectivityManager.EXTRA_NETWORK_INFO;
import static com.rapidftr.CustomTestRunner.createUser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class RapidFtrActivityTest {

    @Test
    public void shouldNotRenderMenuWhenUserIsNotLoggedIn() throws IOException {
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setCurrentUser(null);
        RapidFtrActivity loginActivity = new LoginActivity();

        Menu menu = mock(Menu.class);
        doReturn(mock(MenuItem.class)).when(menu).getItem(anyInt());
        boolean showMenu = loginActivity.onCreateOptionsMenu(menu);

        assertThat(showMenu, is(false));
    }

    @Test
    public void shouldRenderMenuWhenUserIsLoggedIn() throws IOException {
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setCurrentUser(createUser());
        RapidFtrActivity loginActivity = new LoginActivity();
        Menu menu = mock(Menu.class);
        doReturn(mock(MenuItem.class)).when(menu).getItem(anyInt());
        boolean showMenu = loginActivity.onCreateOptionsMenu(menu);

        assertThat(showMenu, is(true));
    }

    @Test
    public void shouldFinishActivityOnLogout() throws IOException {
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setCurrentUser(createUser());
        RapidFtrActivity loginActivity = new LoginActivity();
        MenuItem menuItem = mock(MenuItem.class);
        given(menuItem.getItemId()).willReturn(R.id.logout);

        loginActivity.onOptionsItemSelected(menuItem);
        assertThat(loginActivity.isFinishing(), is(true));
    }

    @Test
    public void shouldCancelTheAsyncTaskIfCancelSynMenuIsClicked(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SynchronisationAsyncTask mockAsyncTask = mock(SynchronisationAsyncTask.class);
        instance.setSyncTask(mockAsyncTask);

        RapidFtrActivity mainActivity = new MainActivity();
        MenuItem cancelSynAll = mock(MenuItem.class);
        doReturn(R.id.cancel_synchronize_all).when(cancelSynAll).getItemId();

        mainActivity.onOptionsItemSelected(cancelSynAll);

        verify(mockAsyncTask).cancel(false);
    }

    @Test
    public void shouldNotThrowExceptionIfAsyncTaskIsNull(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setSyncTask(null);

        RapidFtrActivity mainActivity = new MainActivity();
        MenuItem cancelSynAll = mock(MenuItem.class);
        doReturn(R.id.cancel_synchronize_all).when(cancelSynAll).getItemId();

        mainActivity.onOptionsItemSelected(cancelSynAll);
    }

    @Test
    public void shouldSetTheMenuBasedOnAsynTask() throws IOException {
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SynchronisationAsyncTask mockAsyncTask = mock(SynchronisationAsyncTask.class);
        instance.setSyncTask(mockAsyncTask);
        instance.setCurrentUser(createUser());

        Menu mockMenu = mock(Menu.class);
        MenuItem syncAllMenuItem = mock(MenuItem.class);
        MenuItem cancelSyncAllMenuItem = mock(MenuItem.class);
        doReturn(syncAllMenuItem).when(mockMenu).getItem(0);
        doReturn(cancelSyncAllMenuItem).when(mockMenu).getItem(1);

        RapidFtrActivity mainActivity = new MainActivity();

        mainActivity.onCreateOptionsMenu(mockMenu);
        verify(syncAllMenuItem).setVisible(false);
        verify(cancelSyncAllMenuItem).setVisible(true);

        instance.setSyncTask(null);
        mainActivity.onCreateOptionsMenu(mockMenu);
        verify(syncAllMenuItem).setVisible(true);
        verify(cancelSyncAllMenuItem).setVisible(false);
    }

    @Test
    public void shouldPromptUserWhenAttemptingToLogOutWhileSyncIsActive(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SynchronisationAsyncTask mockAsyncTask = mock(SynchronisationAsyncTask.class);
        instance.setSyncTask(mockAsyncTask);

        RapidFtrActivity mainActivity = new MainActivity();
        MenuItem cancelSynAll = mock(MenuItem.class);
        doReturn(R.id.cancel_synchronize_all).when(cancelSynAll).getItemId();

        mainActivity.onOptionsItemSelected(cancelSynAll);

        verify(mockAsyncTask).cancel(false);
    }

    @Test
    public void shouldSetCurrentContextWhileCreatingMenu() throws IOException {
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SynchronisationAsyncTask mockSyncAll = mock(SynchronisationAsyncTask.class);
        instance.setSyncTask(mockSyncAll);
        instance.setCurrentUser(createUser());

        RapidFtrActivity mainActivity = new MainActivity();
        Menu mockMenu = mock(Menu.class);
        when(mockMenu.getItem(anyInt())).thenReturn(mock(MenuItem.class));
	    mainActivity.onCreateOptionsMenu(mockMenu);
	    verify(mockSyncAll).setContext(mainActivity);
    }

    @Test
    public void shouldShowToastMsgIfSyncIsInProgressAndNetworkLost(){
        RapidFtrActivity rapidFtrActivity = spy(new MainActivity());
        BroadcastReceiver receiver = rapidFtrActivity.getBroadcastReceiver();
        Intent mockIntent = mock(Intent.class);
        NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
        doReturn(mockNetworkInfo).when(mockIntent).getParcelableExtra(EXTRA_NETWORK_INFO);
        doReturn(false).when(mockNetworkInfo).isConnected();
        RapidFtrApplication.getApplicationInstance().setAsyncTaskWithDialog(new AsyncTaskWithDialog() {
            @Override
            public void cancel() {
            }

            @Override
            protected Object doInBackground(Object... objects) {
                return null;
            }
        });
        receiver.onReceive(rapidFtrActivity, mockIntent);
        MatcherAssert.assertThat(ShadowToast.getTextOfLatestToast(), equalTo(rapidFtrActivity.getString(R.string.network_down)));

    }

    @Test
    public void shouldRenderMenuWithOptionsMenuLayout(){
        RapidFtrActivity rapidFtrActivity = new MainActivity();
        RapidFtrActivity spyRapidFtrActivity = spy(rapidFtrActivity);
        Menu menu = mock(Menu.class);
        MenuInflater menuInflater = mock(MenuInflater.class);
        when(menu.getItem(0)).thenReturn(mock(MenuItem.class));
        when(menu.getItem(1)).thenReturn(mock(MenuItem.class));
        when(spyRapidFtrActivity.getMenuInflater()).thenReturn(menuInflater);
        spyRapidFtrActivity.onCreateOptionsMenu(menu);
        verify(menuInflater).inflate(R.menu.options_menu, menu);
    }
}
