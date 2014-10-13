package com.rapidftr.adapter;

import android.content.Context;
import android.widget.Toast;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.Repository;
import org.json.JSONException;

import java.util.List;

import static com.rapidftr.adapter.PaginatedScrollListener.DEFAULT_PAGE_SIZE;

public class Scroller<T extends BaseModel> {

    private int visibleItemThreshold = 5;
    private int currentPage = 0;
    private int numberOfPreviouslyLoadedItems = 0;
    private boolean loading = true;
    private int previousPageNumber = 0;

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
            List<T> records = repository.getRecordsForPage(previousPageNumber, currentPage);
            highlightedFieldsViewAdapter.addAll(records);
        }
    }

    public void updateAdapterSizeAndPageNumbers() {
        if (isLoadingCompleted()) {
            loading = false;
            numberOfPreviouslyLoadedItems = numberOfItemsInAdapter;
            previousPageNumber = currentPage;
            currentPage += DEFAULT_PAGE_SIZE;
        }
    }

    private boolean isLoadingCompleted() {
        return loading && (numberOfItemsInAdapter > numberOfPreviouslyLoadedItems);
    }

    private boolean shouldQueryForMoreData() {
        return !loading && scrolledPastVisibleItemThreshold();
    }

    private boolean scrolledPastVisibleItemThreshold() {
        return (numberOfItemsInAdapter - numberOfVisibleItems) <= (firstVisibleItem + visibleItemThreshold);
    }

}
