package com.rapidftr.activity;

import android.content.Context;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.features.FeatureToggle;
import com.rapidftr.model.Child;
import com.rapidftr.service.FormService;
import com.rapidftr.utils.SpyActivityController;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class RegisterChildActivityTest {

    private ActivityController<RegisterChildActivity> activityController;
    private RegisterChildActivity activity;
    private FeatureToggle featureToggle;

    @Before
    public void setUp() {
        activityController = SpyActivityController.of(RegisterChildActivity.class);
        activity = activityController.attach().get();
        featureToggle = new FeatureToggle(Robolectric.application.getSharedPreferences(RapidFtrApplication.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE));

        RapidFtrApplication application = RapidFtrApplication.getApplicationInstance();
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(new FormService(application)).when(mockInjector).getInstance(FormService.class);
        doReturn(featureToggle).when(mockInjector).getInstance(FeatureToggle.class);
    }

    @Test
    public void testRenderLayout() throws JSONException {
        activityController.create();
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
