package com.rapidftr.activity;

import android.view.Menu;
import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(CustomTestRunner.class)
public class RapidFtrActivityTest {

    @Test
    public void shouldNotRenderMenuWhenUserIsNotLoggedIn(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setLoggedIn(false);
        RapidFtrActivity loginActivity = new LoginActivity();

        boolean showMenu = loginActivity.onCreateOptionsMenu(mock(Menu.class));

        assertThat(showMenu, is(false));
    }

    @Test
    public void shouldRenderMenuWhenUserIsLoggedIn(){
        RapidFtrApplication instance = RapidFtrApplication.getApplicationInstance();
        instance.setLoggedIn(true);
        RapidFtrActivity loginActivity = new LoginActivity();

        boolean showMenu = loginActivity.onCreateOptionsMenu(mock(Menu.class));

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

}
