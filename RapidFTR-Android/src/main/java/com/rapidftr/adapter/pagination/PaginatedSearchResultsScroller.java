package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildSearch;
import org.json.JSONException;

import static com.rapidftr.adapter.pagination.ViewAllChildrenPaginatedScrollListener.DEFAULT_PAGE_SIZE;

public class PaginatedSearchResultsScroller extends Scroller{

    private ChildSearch childSearch;
    private HighlightedFieldsViewAdapter<Child> adapter;

    public PaginatedSearchResultsScroller(ChildSearch childSearch, HighlightedFieldsViewAdapter<Child> adapter) {
        super();
        this.childSearch = childSearch;
        this.adapter = adapter;
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if (shouldQueryForMoreData()) {
            adapter.addAll(childSearch.getRecordsForNextPage(adapter.getCount(), adapter.getCount() + DEFAULT_PAGE_SIZE));
        }
    }
}
