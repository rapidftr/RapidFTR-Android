package com.rapidftr.adapter;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ScrollerTest {

    private ChildRepository repository;
    private HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter;
    private Scroller scroller;

    @Before
    public void setUp() throws Exception {
        repository = null;
        highlightedFieldsViewAdapter = null;
    }

    @Test
    public void shouldLoadRecordsForNextPage() throws JSONException {
        repository = mock(ChildRepository.class);
        highlightedFieldsViewAdapter = mock(HighlightedFieldsViewAdapter.class);
        List<Child> children = Arrays.asList(new Child("id", "user", "{\"name\": \"Foo Bar\"}"));
        when(repository.getRecordsForPage(anyInt(), anyInt())).thenReturn(children);

        scroller = spy(new Scroller(repository, highlightedFieldsViewAdapter, 24, 6, 30));
        doReturn(true).when(scroller).shouldQueryForMoreData();
        scroller.loadRecordsForNextPage();

        verify(repository, atLeastOnce()).getRecordsForPage(anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, atLeastOnce()).addAll(children);
    }

    @Test
    public void scrolledPastVisibleItemThresholdShouldReturnFalseWhenViewingFirstRecordsInListView(){
        scroller = new Scroller(repository, highlightedFieldsViewAdapter, 0, 6, 30);
        assertFalse(scroller.scrolledPastVisibleItemThreshold());
    }

    @Test
    public void scrolledPastVisibleItemThresholdShouldReturnFalseWhenViewingRecordsInMiddleOfListView(){
        scroller = new Scroller(repository, highlightedFieldsViewAdapter, 17, 6, 30);
        assertFalse(scroller.scrolledPastVisibleItemThreshold());
    }

    @Test
    public void scrolledPastVisibleItemThresholdShouldReturnTrueWhenViewingLastPageRecordsOfListView(){
        scroller = new Scroller(repository, highlightedFieldsViewAdapter, 24, 6, 30);
        assertTrue(scroller.scrolledPastVisibleItemThreshold());
    }

    @Test
    public void shouldQueryForMoreDataReturnsTrueWhenVisibleItemsThresholdIsExceeded(){
        scroller = new Scroller(repository, highlightedFieldsViewAdapter, 24, 6, 30);
        assertTrue(scroller.scrolledPastVisibleItemThreshold());
        assertTrue(scroller.shouldQueryForMoreData());
    }

    @Test
    public void shouldQueryForMoreDataReturnsFalseWhenVisibleItemsThresholdIsExceeded(){
        scroller = new Scroller(repository, highlightedFieldsViewAdapter, 18, 6, 30);
        assertFalse(scroller.scrolledPastVisibleItemThreshold());
        assertFalse(scroller.shouldQueryForMoreData());
    }
}
