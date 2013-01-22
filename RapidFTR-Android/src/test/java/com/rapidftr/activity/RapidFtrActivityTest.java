package com.rapidftr.activity;

import android.view.Menu;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.task.SyncAllDataAsyncTask;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class RapidFtrActivityTest {

    @Test
    public void shouldNotRenderMenuWhenUserIsNotLoggedIn(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setLoggedIn(false);
        RapidFtrActivity loginActivity = new LoginActivity();

        Menu menu = mock(Menu.class);
        doReturn(mock(MenuItem.class)).when(menu).getItem(anyInt());
        boolean showMenu = loginActivity.onCreateOptionsMenu(menu);

        assertThat(showMenu, is(false));
    }

    @Test
    public void shouldRenderMenuWhenUserIsLoggedIn(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setLoggedIn(true);
        RapidFtrActivity loginActivity = new LoginActivity();
        Menu menu = mock(Menu.class);
        doReturn(mock(MenuItem.class)).when(menu).getItem(anyInt());
        boolean showMenu = loginActivity.onCreateOptionsMenu(menu);

        assertThat(showMenu, is(true));
    }

    @Test
    public void shouldFinishActivityOnLogout(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setLoggedIn(true);
        RapidFtrActivity loginActivity = new LoginActivity();
        MenuItem menuItem = mock(MenuItem.class);
        given(menuItem.getItemId()).willReturn(R.id.logout);

        loginActivity.onOptionsItemSelected(menuItem);
        assertThat(loginActivity.isFinishing(), is(true));
    }

    @Test
    public void shouldCancelTheAsyncTaskIfCancelSynMenuIsClicked(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SyncAllDataAsyncTask mockAsyncTask = mock(SyncAllDataAsyncTask.class);
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
    public void shouldSetTheMenuBasedOnAsynTask(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SyncAllDataAsyncTask mockAsyncTask = mock(SyncAllDataAsyncTask.class);
        instance.setSyncTask(mockAsyncTask);
        instance.setLoggedIn(true);

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
        SyncAllDataAsyncTask mockAsyncTask = mock(SyncAllDataAsyncTask.class);
        instance.setSyncTask(mockAsyncTask);

        RapidFtrActivity mainActivity = new MainActivity();
        MenuItem cancelSynAll = mock(MenuItem.class);
        doReturn(R.id.cancel_synchronize_all).when(cancelSynAll).getItemId();

        mainActivity.onOptionsItemSelected(cancelSynAll);

        verify(mockAsyncTask).cancel(false);
    }

    @Test
    public void shouldSetCurrentContextWhileCreatingMenu(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        SyncAllDataAsyncTask mockSyncAll = mock(SyncAllDataAsyncTask.class);
        instance.setSyncTask(mockSyncAll);
        instance.setLoggedIn(true);

        RapidFtrActivity mainActivity = new MainActivity();
        Menu mockMenu = mock(Menu.class);
        when(mockMenu.getItem(anyInt())).thenReturn(mock(MenuItem.class));
        mainActivity.onCreateOptionsMenu(mockMenu);
        verify(mockSyncAll).setContext(mainActivity);
    }
}
