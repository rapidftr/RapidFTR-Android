package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;

import java.util.List;

import static com.rapidftr.adapter.pagination.ViewAllChildrenPaginatedScrollListener.DEFAULT_PAGE_SIZE;

public class ViewAllChildScroller extends Scroller {

    private final ChildRepository repository;
    private final HighlightedFieldsViewAdapter<Child> adapter;

    public ViewAllChildScroller(ChildRepository repository, HighlightedFieldsViewAdapter<Child> adapter) {
        super();
        this.repository = repository;
        this.adapter = adapter;
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if (shouldQueryForMoreData()) {
            List<Child> records = repository.getRecordsBetween(adapter.getCount(), adapter.getCount() + DEFAULT_PAGE_SIZE);
            adapter.addAll(records);
        }
    }
}
