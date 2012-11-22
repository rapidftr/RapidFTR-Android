package com.rapidftr.activity;

import android.view.MenuItem;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class EditChildActivityTest {

    EditChildActivity activity;

    @Before
    public void setUp() {
        activity = spy(new EditChildActivity());
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
    public void shouldSyncAndShowChildRecord() throws IOException, JSONException {
        Child child = mock(Child.class);
        ChildService service = mock(ChildService.class);

        doReturn(child).when(service).sync(child);
        doReturn(service).when(activity).inject(ChildService.class);

        activity.child = child;
        activity.sync();

        verify(service).sync(child);
        verify(activity).rebuild();
    }

}
