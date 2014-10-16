package com.rapidftr.adapter.pagination;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ViewAllEnquiryScrollerTest {

    private EnquiryRepository repository;
    private HighlightedFieldsViewAdapter<Enquiry> highlightedFieldsViewAdapter;
    private ViewAllEnquiryScroller scroller;

    @Before
    public void setUp() throws Exception {
        repository = mock(EnquiryRepository.class);
        highlightedFieldsViewAdapter = mock(HighlightedFieldsViewAdapter.class);
        scroller = spy(new ViewAllEnquiryScroller(repository, highlightedFieldsViewAdapter));
    }

    @Test
    public void shouldLoadRecordsForNextPage() throws JSONException {
        List<Enquiry> enquiries = Arrays.asList(new Enquiry("{\"name\": \"Foo Bar\"}"));
        when(repository.getRecordsBetween(anyInt(), anyInt())).thenReturn(enquiries);
        doReturn(true).when(scroller).shouldQueryForMoreData();

        scroller.loadRecordsForNextPage();

        verify(repository, atLeastOnce()).getRecordsBetween(anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, atLeastOnce()).addAll(enquiries);
    }

    @Test
    public void shouldNotLoadRecordsForNextPage() throws JSONException {
        doReturn(false).when(scroller).shouldQueryForMoreData();

        scroller.loadRecordsForNextPage();

        verify(repository, times(0)).getRecordsBetween(anyInt(), anyInt());
        verify(highlightedFieldsViewAdapter, times(0)).addAll(anyList());
    }

}