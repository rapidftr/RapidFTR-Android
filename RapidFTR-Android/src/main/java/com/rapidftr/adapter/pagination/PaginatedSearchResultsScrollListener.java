package com.rapidftr.adapter.pagination;

import android.widget.AbsListView;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildSearch;
import org.json.JSONException;

public class PaginatedSearchResultsScrollListener implements AbsListView.OnScrollListener {

    private PaginatedSearchResultsScroller scroller;

    public PaginatedSearchResultsScrollListener(
            ChildSearch childSearch, HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter) {
        scroller = new PaginatedSearchResultsScroller(childSearch, highlightedFieldsViewAdapter);

    }

    @Override
    public void onScroll(AbsListView absListView,
                         int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        scroller.updateRecordNumbers(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);

        try {
            scroller.loadRecordsForNextPage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}
}
