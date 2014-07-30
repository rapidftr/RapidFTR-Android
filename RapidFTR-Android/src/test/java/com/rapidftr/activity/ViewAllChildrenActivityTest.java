package com.rapidftr.activity;

import android.widget.ListView;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.SpyActivityController;
import com.rapidftr.utils.TestInjectionModule;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewAllChildrenActivityTest {
    private ActivityController<ViewAllChildrenActivity> activityController;
    protected ViewAllChildrenActivity activity;

    @Mock
    private ChildRepository childRepository;

    @Before
    public void setUp() {
        initMocks(this);
        activityController = SpyActivityController.of(ViewAllChildrenActivity.class);
        activity = activityController.attach().get();
        TestInjectionModule module = new TestInjectionModule();
        module.addBinding(DatabaseHelper.class, ShadowSQLiteHelper.getInstance());
        module.addBinding(ChildRepository.class, childRepository);
        TestInjectionModule.setUp(this, module);
    }

    @Test
    public void shouldListChildrenCreatedByTheLoggedInUser() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        children.add(new Child("id1", "user1", "{ \"name\" : \"child1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        when(childRepository.getChildrenByOwner()).thenReturn(children);

        activityController.create();
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
    }

    @Test
    public void shouldShowNoChildMessageWhenNoChildrenPresent() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        when(childRepository.getChildrenByOwner()).thenReturn(children);

        activityController.create();
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNotNull(listView.getEmptyView());
    }

}
