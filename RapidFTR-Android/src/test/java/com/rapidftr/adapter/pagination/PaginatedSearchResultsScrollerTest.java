package com.rapidftr.adapter.pagination;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.ChildSearch;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class PaginatedSearchResultsScrollerTest {

    private ChildRepository repository;
    private HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter;
    private PaginatedSearchResultsScroller scroller;
    private ChildSearch childSearch;

    @Before
    public void setUp() throws Exception {
        this.repository = mock(ChildRepository.class);
        this.highlightedFieldsViewAdapter = mock(HighlightedFieldsViewAdapter.class);
    }

    @Test
    public void shouldLoadRecordsForNextPage() throws JSONException {
        childSearch = new ChildSearch("", repository, null);
        scroller = spy(new PaginatedSearchResultsScroller(childSearch, highlightedFieldsViewAdapter));
        List<Child> children = Arrays.asList(new Child("id", "user", "{\"name\": \"Foo Bar\"}"));
        when(repository.getChildrenMatchingStringBetween(anyString(), anyInt(), anyInt())).thenReturn(children);
        doReturn(true).when(scroller).shouldQueryForMoreData();

        scroller.loadRecordsForNextPage();

        verify(repository, times(1)).getChildrenMatchingStringBetween(anyString(), anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, times(1)).addAll(children);
    }

    @Test
    public void shouldNotLoadRecordsForNextPage() throws JSONException {
        scroller = spy(new PaginatedSearchResultsScroller(childSearch, highlightedFieldsViewAdapter));
        doReturn(false).when(scroller).shouldQueryForMoreData();

        scroller.loadRecordsForNextPage();

        verify(repository, times(0)).getChildrenMatchingStringBetween(anyString(), anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, times(0)).addAll(anyList());
    }

}
