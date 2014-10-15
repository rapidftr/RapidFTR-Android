package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildSearch;
import org.json.JSONException;

public class PaginatedSearchResultsScroller extends Scroller{

    private ChildSearch childSearch;
    private HighlightedFieldsViewAdapter<Child> adapter;

    public PaginatedSearchResultsScroller(ChildSearch childSearch, HighlightedFieldsViewAdapter<Child> adapter,
                                          int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        super(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        this.childSearch = childSearch;
        this.adapter = adapter;
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if (shouldQueryForMoreData()) {
            loading = true;
            adapter.addAll(childSearch.getRecordsForNextPage(previousPageNumber, nextPageNumber));
        }
    }
}
