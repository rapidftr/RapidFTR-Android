package com.rapidftr.activity;

import android.widget.ListView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ViewAllChildrenActivityTest {
    protected ViewAllChildrenActivity activity;

    @Mock
    private ChildRepository childRepository;

    @Before
    public void setUp() {
        initMocks(this);
        activity = spy(new ViewAllChildrenActivity());
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(childRepository).when(mockInjector).getInstance(ChildRepository.class);
    }

    @Test
    public void shouldListChildrenCreatedByTheLoggedInUser() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        children.add(new Child("id1", "user1", "{ \"name\" : \"child1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        when(childRepository.getChildrenByOwner()).thenReturn(children);

        activity.onCreate(null);
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
    }

    @Test
    public void shouldShowNoChildMessageWhenNoChildrenPresent() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        when(childRepository.getChildrenByOwner()).thenReturn(children);

        activity.onCreate(null);
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNotNull(listView.getEmptyView());
    }

}
