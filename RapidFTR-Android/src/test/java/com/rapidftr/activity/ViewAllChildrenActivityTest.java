package com.rapidftr.activity;

import android.content.Context;
import android.widget.ListView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.features.FeatureToggle;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.FormService;
import com.rapidftr.utils.SpyActivityController;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewAllChildrenActivityTest {
    private ActivityController<ViewAllChildrenActivity> activityController;
    protected ViewAllChildrenActivity activity;

    @Mock
    private ChildRepository childRepository;
    private FeatureToggle featureToggle;

    @Before
    public void setUp() {
        initMocks(this);
        activityController = SpyActivityController.of(ViewAllChildrenActivity.class);
        activity = activityController.attach().get();
        featureToggle = new FeatureToggle(Robolectric.application.getSharedPreferences(RapidFtrApplication.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE));

        RapidFtrApplication application = RapidFtrApplication.getApplicationInstance();

        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(new FormService(application)).when(mockInjector).getInstance(FormService.class);
        doReturn(childRepository).when(mockInjector).getInstance(ChildRepository.class);
        doReturn(featureToggle).when(mockInjector).getInstance(FeatureToggle.class);
    }

    @Test
    public void shouldListChildrenCreatedByTheLoggedInUser() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        children.add(new Child("id1", "user1", "{ \"name\" : \"child1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        when(childRepository.getRecordsForFirstPage()).thenReturn(children);

        activityController.create();
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
    }

    @Test
    public void shouldShowNoChildMessageWhenNoChildrenPresent() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        when(childRepository.allCreatedByCurrentUser()).thenReturn(children);

        activityController.create();
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNotNull(listView.getEmptyView());
    }

}
