package com.rapidftr.activity;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class RegisterChildActivityTest {

    private RegisterChildActivity activity;

    @Before
    public void setUp() {
        activity = spy(new RegisterChildActivity());
    }

    @Test
    public void testRenderLayout() throws JSONException {
        activity.onCreate(null);
        verify(activity).setContentView(R.layout.activity_register_child);
    }

    @Test
    public void testInitializeChild() throws JSONException {
        activity.initializeData(null);
        assertThat(activity.child, equalTo(new Child()));
    }

    @Test
    public void testSaveListener() throws JSONException {
        doReturn(null).when(activity).save();
        activity.initializeView();

        activity.findViewById(R.id.submit).performClick();
        verify(activity).save();
    }


}
