package com.rapidftr.adapter;

import android.widget.AbsListView;
import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.Repository;
import org.json.JSONException;

import java.util.List;


public class PaginatedScrollListener<T extends BaseModel> implements AbsListView.OnScrollListener{

    private Repository<T> repository;
    private HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter;
    private boolean loading = true;
    private int visibleItemThreshold = 5;
    private int currentPage = 0;
    private int numberOfPreviouslyLoadedItems = 0;
    private int previousPageNumber = 0;

    public PaginatedScrollListener(Repository<T> repository,
                                   HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int numberOfVisibleItems, int numberOfItemsInAdapter) {

        updateAdapterSizeAndPageNumbers(numberOfItemsInAdapter);

        try {
            loadRecordsForNextPage(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateAdapterSizeAndPageNumbers(int numberOfItemsInAdapter) {
        if (isLoadingCompleted(numberOfItemsInAdapter)) {
            loading = false;
            highlightedFieldsViewAdapter.removeFirstPage();
            numberOfPreviouslyLoadedItems = numberOfItemsInAdapter;
            previousPageNumber = currentPage;
            currentPage += 30;
        }
    }

    private boolean isLoadingCompleted(int numberOfItemsInAdapter) {
        return loading && (numberOfItemsInAdapter > numberOfPreviouslyLoadedItems);
    }

    private void loadRecordsForNextPage(int firstVisibleItem, int numberOfVisibleItems,
                                        int numberOfItemsInAdapter) throws JSONException {
        if (shouldQueryForMoreData(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter)) {
            loading = true;
            List<T> records = repository.getRecordsForPage(previousPageNumber, currentPage);
            highlightedFieldsViewAdapter.addAll(records);
        }
    }

    private boolean shouldQueryForMoreData(int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        return !loading && scrollPositionGreaterThanVisibleItemThreshold(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
    }

    private boolean scrollPositionGreaterThanVisibleItemThreshold(int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        return (numberOfItemsInAdapter - numberOfVisibleItems) <= (firstVisibleItem + visibleItemThreshold);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}
}
