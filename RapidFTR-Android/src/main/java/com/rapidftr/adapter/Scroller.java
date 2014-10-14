package com.rapidftr.adapter;

import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.Repository;
import org.json.JSONException;

import static com.rapidftr.adapter.ViewAllChildrenPaginatedScrollListener.DEFAULT_PAGE_SIZE;

public abstract class Scroller<T extends BaseModel> {

    //we have already loaded the first 30 records
    protected int nextPageNumber = 60;
    protected int previousPageNumber = 30;

    private int visibleItemThreshold = 5;
    private int numberOfPreviouslyLoadedItems = 0;
    protected boolean loading = true;

    private int firstVisibleItem;
    private int numberOfVisibleItems;
    private int numberOfItemsInAdapter;

    protected Repository<T> repository;
    protected HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter;

    public Scroller(Repository<T> repository, HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter,
                    int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
        this.firstVisibleItem = firstVisibleItem;
        this.numberOfVisibleItems = numberOfVisibleItems;
        this.numberOfItemsInAdapter = numberOfItemsInAdapter;
    }

    public abstract void loadRecordsForNextPage() throws JSONException;

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
