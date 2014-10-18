package com.rapidftr.adapter.pagination;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ViewAllChildScrollerTest {

    private ChildRepository repository;
    private HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter;
    private ViewAllChildScroller scroller;

    @Before
    public void setUp() throws Exception {
        repository = mock(ChildRepository.class);
        highlightedFieldsViewAdapter = mock(HighlightedFieldsViewAdapter.class);
        scroller = spy(new ViewAllChildScroller(repository, highlightedFieldsViewAdapter));
    }

    @Test
    public void shouldLoadRecordsForNextPage() throws JSONException {
        List<Child> children = Arrays.asList(new Child("id", "user", "{\"name\": \"Foo Bar\"}"));
        when(repository.getRecordsBetween(anyInt(), anyInt())).thenReturn(children);
        doReturn(true).when(scroller).shouldQueryForMoreData();

        scroller.loadRecordsForNextPage();

        verify(repository, atLeastOnce()).getRecordsBetween(anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, atLeastOnce()).addAll(children);
    }

    @Test
    public void shouldNotLoadRecordsForNextPage() throws JSONException {
        doReturn(false).when(scroller).shouldQueryForMoreData();

        scroller.loadRecordsForNextPage();

        verify(repository, times(0)).getRecordsBetween(anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, times(0)).addAll(anyList());
    }

}
