//package com.rapidftr.adapter.pagination;
//
//import com.rapidftr.CustomTestRunner;
//import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
//import com.rapidftr.model.Child;
//import com.rapidftr.repository.ChildRepository;
//import org.json.JSONException;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Mockito.*;
//
//@RunWith(CustomTestRunner.class)
//public class ViewAllChildScrollerTest {
//
//    private ChildRepository repository;
//    private HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter;
//    private ViewAllChildScroller scroller;
//
//    @Before
//    public void setUp() throws Exception {
//        repository = null;
//        highlightedFieldsViewAdapter = null;
//    }
//
//    @Test
//    public void shouldLoadRecordsForNextPage() throws JSONException {
//        repository = mock(ChildRepository.class);
//        highlightedFieldsViewAdapter = mock(HighlightedFieldsViewAdapter.class);
//        List<Child> children = Arrays.asList(new Child("id", "user", "{\"name\": \"Foo Bar\"}"));
//        when(repository.getRecordsBetween(anyInt(), anyInt())).thenReturn(children);
//        scroller = spy(new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, 24, 6, 30));
//        doReturn(true).when(scroller).shouldQueryForMoreData();
//
//        scroller.loadRecordsForNextPage();
//
//        verify(repository, atLeastOnce()).getRecordsBetween(anyInt(), anyInt());
//        verify(highlightedFieldsViewAdapter, atLeastOnce()).addAll(children);
//    }
//
//    @Test
//    public void scrolledPastVisibleItemThresholdShouldReturnFalseWhenViewingFirstRecordsInListView(){
//        scroller = new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, 0, 6, 30);
//        int numberOfRecordsSeen = scroller.firstVisibleItem + scroller.visibleItemThreshold;
//        int recordNumberToTriggerLoad = scroller.numberOfItemsInAdapter - scroller.numberOfVisibleItems;
//        assertFalse(recordNumberToTriggerLoad <= numberOfRecordsSeen);
//    }
//
//    @Test
//    public void scrolledPastVisibleItemThresholdShouldReturnFalseWhenViewingRecordsInMiddleOfListView(){
//        scroller = new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, 17, 6, 30);
//        int numberOfRecordsSeen = scroller.firstVisibleItem + scroller.visibleItemThreshold;
//        int recordNumberToTriggerLoad = scroller.numberOfItemsInAdapter - scroller.numberOfVisibleItems;
//        assertFalse(recordNumberToTriggerLoad <= numberOfRecordsSeen);
//    }
//
//    @Test
//    public void scrolledPastVisibleItemThresholdShouldReturnTrueWhenViewingLastPageRecordsOfListView(){
//        scroller = new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, 24, 6, 30);
//        int numberOfRecordsSeen = scroller.firstVisibleItem + scroller.visibleItemThreshold;
//        int recordNumberToTriggerLoad = scroller.numberOfItemsInAdapter - scroller.numberOfVisibleItems;
//        assertTrue(recordNumberToTriggerLoad <= numberOfRecordsSeen);
//    }
//
//    @Test
//    public void shouldQueryForMoreDataReturnsTrueWhenVisibleItemsThresholdIsExceeded(){
//        scroller = new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, 24, 6, 30);
//        int numberOfRecordsSeen = scroller.firstVisibleItem + scroller.visibleItemThreshold;
//        int recordNumberToTriggerLoad = scroller.numberOfItemsInAdapter - scroller.numberOfVisibleItems;
//        assertTrue(recordNumberToTriggerLoad <= numberOfRecordsSeen);
//        assertTrue(scroller.shouldQueryForMoreData());
//    }
//
//    @Test
//    public void shouldQueryForMoreDataReturnsFalseWhenVisibleItemsThresholdIsExceeded(){
//        scroller = new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, 18, 6, 30);
//        int numberOfRecordsSeen = scroller.firstVisibleItem + scroller.visibleItemThreshold;
//        int recordNumberToTriggerLoad = scroller.numberOfItemsInAdapter - scroller.numberOfVisibleItems;
//        assertFalse(recordNumberToTriggerLoad <= numberOfRecordsSeen);
//        assertFalse(scroller.shouldQueryForMoreData());
//    }
//}
