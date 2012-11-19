package com.rapidftr.activity;

import android.widget.ListView;
import android.widget.TextView;
import com.google.inject.Injector;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;
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
public class SearchActivityTest {
    protected SearchActivity activity;

    @Mock
    private ChildService childService;

    @Before
    public void setUp() {
        initMocks(this);
        activity = spy(new SearchActivity());
        Injector mockInjector = mock(Injector.class);
        doReturn(mockInjector).when(activity).getInjector();
        doReturn(childService).when(mockInjector).getInstance(ChildService.class);

    }

    @Test
    public void shouldListChildrenForSearchedString() throws JSONException {
        List<Child> searchResults = new ArrayList<Child>();
        searchResults.add(new Child("id1", "user1", "{ \"name\" : \"child1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        String searchString = "Hild";
        when(childService.searchChildrenInDB(searchString)).thenReturn(searchResults);

        activity.onCreate(null);
        TextView textView = (TextView) activity.findViewById(R.id.search_text);
        textView.setText(searchString);
        activity.findViewById(R.id.search_btn).performClick();
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNull(listView.getEmptyView());
        assertNotNull(listView.getItemAtPosition(0));
    }

    @Test
    public void shouldShowEmptyViewForNoSearchResults() throws JSONException {
        List<Child> searchResults = new ArrayList<Child>();
        String searchString = "Hild";
        when(childService.searchChildrenInDB(searchString)).thenReturn(searchResults);

        activity.onCreate(null);
        TextView textView = (TextView) activity.findViewById(R.id.search_text);
        textView.setText(searchString);
        activity.findViewById(R.id.search_btn).performClick();
        ListView listView = (ListView) activity.findViewById(R.id.child_list);
        assertNotNull(listView.getEmptyView());
    }

}
