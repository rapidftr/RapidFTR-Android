package com.rapidftr.adapter;

import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.Repository;
import org.json.JSONException;

import java.util.List;

import static com.rapidftr.adapter.PaginatedScrollListener.DEFAULT_PAGE_SIZE;

public class Scroller<T extends BaseModel> {

    //we have already loaded the first 30 records
    private int nextPageNumber = 60;
    private int previousPageNumber = 30;

    private int visibleItemThreshold = 5;
    private int numberOfPreviouslyLoadedItems = 0;
    private boolean loading = true;

    private int firstVisibleItem;
    private int numberOfVisibleItems;
    private int numberOfItemsInAdapter;

    private Repository<T> repository;
    private HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter;

    public Scroller(Repository<T> repository, HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter,
                    int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
        this.firstVisibleItem = firstVisibleItem;
        this.numberOfVisibleItems = numberOfVisibleItems;
        this.numberOfItemsInAdapter = numberOfItemsInAdapter;
    }

    public void loadRecordsForNextPage() throws JSONException {
        if (shouldQueryForMoreData()) {
            loading = true;
            List<T> records = repository.getRecordsBetween(previousPageNumber, nextPageNumber);
            highlightedFieldsViewAdapter.addAll(records);
        }
    }

    public void updatePageNumbers() {
        if (isLoadingCompleted()) {
            loading = false;
            numberOfPreviouslyLoadedItems = numberOfItemsInAdapter;
            previousPageNumber = nextPageNumber;
            nextPageNumber += DEFAULT_PAGE_SIZE;
        }
    }

    protected boolean isLoadingCompleted() {
        return loading && (numberOfItemsInAdapter > numberOfPreviouslyLoadedItems);
    }

    protected boolean shouldQueryForMoreData() {
        return !loading && scrolledPastVisibleItemThreshold();
    }

    protected boolean scrolledPastVisibleItemThreshold() {
        boolean result = numberOfRecordsNotYetSeen() <= numberOFRecordsSeenSoFar();
        if(result) {
            loading = false;
        }
        return result;
    }

    private int numberOFRecordsSeenSoFar() {
        return (firstVisibleItem + visibleItemThreshold);
    }

    private int numberOfRecordsNotYetSeen() {
        return numberOfItemsInAdapter - numberOfVisibleItems;
    }
}
