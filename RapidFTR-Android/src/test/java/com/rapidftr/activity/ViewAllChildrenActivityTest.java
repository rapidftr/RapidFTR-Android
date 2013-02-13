package com.rapidftr.activity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
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

    @Test
    public void shouldRenderMenuWithViewChildrenMenuLayout(){
        ViewAllChildrenActivity viewAllChildrenActivity = new ViewAllChildrenActivity();
        RapidFtrActivity spyViewAllChildrenActivity = spy(viewAllChildrenActivity);
        Menu menu = mock(Menu.class);
        MenuInflater menuInflater = mock(MenuInflater.class);
        when(menu.getItem(0)).thenReturn(mock(MenuItem.class));
        when(menu.getItem(1)).thenReturn(mock(MenuItem.class));
        when(spyViewAllChildrenActivity.getMenuInflater()).thenReturn(menuInflater);
        spyViewAllChildrenActivity.onCreate(null);
        spyViewAllChildrenActivity.onCreateOptionsMenu(menu);
        verify(menuInflater).inflate(R.menu.view_children_menu, menu);
    }

    @Test
    public void shouldTestSortChildrenByName() throws JSONException {
        List<Child> children = new ArrayList<Child>();
        children.add(new Child("id1", "user1", "{ \"name\" : \"bbbb\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        children.add(new Child("id2", "user2", "{ \"name\" : \"aaaa\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        ViewAllChildrenActivity viewAllChildrenActivity = spy(new ViewAllChildrenActivity());
        ChildViewAdapter childViewAdapter = spy(new ChildViewAdapter(viewAllChildrenActivity, R.layout.row_child, children));
        viewAllChildrenActivity.setChildViewAdapter(childViewAdapter);
        viewAllChildrenActivity.setChildren(children);
        viewAllChildrenActivity.sortChildrenByName();
        assertThat(childViewAdapter.getItem(0).getName(), equalTo("aaaa"));
        assertThat(childViewAdapter.getItem(1).getName(), equalTo("bbbb"));
        verify(childViewAdapter).notifyDataSetChanged();
      }

    @Test
    public void shouldTestSortChildrenByRecentUpdate() throws JSONException {
        Timestamp timestamp1 = new Timestamp(new Date().getTime());
        Timestamp timestamp2 = new Timestamp(new Date().getTime() + 1000);
        List<Child> children = new ArrayList<Child>();
        children.add(new Child("id1", "user1", "{ \"name\" : \"aaaa\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ], \"last_updated_at\" : \""+timestamp1+"\"}"));
        children.add(new Child("id2", "user2", "{ \"name\" : \"bbbb\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ], \"last_updated_at\": \""+timestamp2+"\"}"));
        ViewAllChildrenActivity viewAllChildrenActivity = spy(new ViewAllChildrenActivity());
        ChildViewAdapter childViewAdapter = spy(new ChildViewAdapter(viewAllChildrenActivity, R.layout.row_child, children));
        viewAllChildrenActivity.setChildViewAdapter(childViewAdapter);
        viewAllChildrenActivity.setChildren(children);
        viewAllChildrenActivity.sortChildrenByRecentUpdate();
        assertThat(childViewAdapter.getItem(0).getName(), equalTo("bbbb"));
        assertThat(childViewAdapter.getItem(1).getName(), equalTo("aaaa"));
        verify(childViewAdapter).notifyDataSetChanged();
    }

}
