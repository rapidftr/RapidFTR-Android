package com.rapidftr.adapter.pagination;

import org.json.JSONException;

public abstract class Scroller {

    private int visibleItemThreshold = 5;

    private int firstVisibleItem;
    private int numberOfVisibleItems;
    private int numberOfItemsInAdapter;

    public Scroller() {
        this.firstVisibleItem = 0;
        this.numberOfVisibleItems = 0;
        this.numberOfItemsInAdapter = 0;
    }

    public abstract void loadRecordsForNextPage() throws JSONException;

    protected boolean shouldQueryForMoreData() {
        int numberOfRecordsSeen = firstVisibleItem + visibleItemThreshold;
        int recordNumberToTriggerLoad = numberOfItemsInAdapter - numberOfVisibleItems;
        return recordNumberToTriggerLoad <= numberOfRecordsSeen;
    }

    public void updateRecordNumbers(int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        this.firstVisibleItem = firstVisibleItem;
        this.numberOfVisibleItems = numberOfVisibleItems;
        this.numberOfItemsInAdapter = numberOfItemsInAdapter;
    }
}
