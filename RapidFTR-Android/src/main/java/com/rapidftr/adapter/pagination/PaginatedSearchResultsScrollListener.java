package com.rapidftr.adapter.pagination;

import android.widget.AbsListView;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildSearch;
import org.json.JSONException;

public class PaginatedSearchResultsScrollListener implements AbsListView.OnScrollListener {

    private PaginatedSearchResultsScroller scroller;
    private ChildSearch childSearch;
    private HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter;

    public PaginatedSearchResultsScrollListener(
            ChildSearch childSearch, HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter) {
        this.childSearch = childSearch;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
    }

    @Override
    public void onScroll(AbsListView absListView,
                         int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {

        scroller = new PaginatedSearchResultsScroller(childSearch, highlightedFieldsViewAdapter,
                firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        scroller.updatePageNumbers();

        try {
            scroller.loadRecordsForNextPage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}
}
