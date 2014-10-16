package com.rapidftr.adapter.pagination;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ScrollerTest {

    private Scroller scroller;

    @Before
    public void setUp(){
        scroller = new Scroller() {
            @Override
            public void loadRecordsForNextPage() throws JSONException {}
        };
    }

    @Test
    public void shouldQueryForMoreDataReturnsTrueWhenThereIsNoRecordInListView(){
        assertTrue(scroller.shouldQueryForMoreData());
    }

    @Test
    public void shouldQueryForMoreDataReturnsTrueReturnTrueWhenViewingLastPageRecordsOfListView(){
        scroller.updateRecordNumbers(24, 6, 30);
        assertTrue(scroller.shouldQueryForMoreData());
    }

}
