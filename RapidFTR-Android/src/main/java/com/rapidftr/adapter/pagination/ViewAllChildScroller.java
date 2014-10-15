package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;

import java.util.List;

public class ViewAllChildScroller extends Scroller {

    private final ChildRepository repository;
    private final HighlightedFieldsViewAdapter<Child> adapter;

    public ViewAllChildScroller(ChildRepository repository, HighlightedFieldsViewAdapter<Child> adapter,
                                int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        super(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        this.repository = repository;
        this.adapter = adapter;
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if (shouldQueryForMoreData()) {
            loading = true;
            List<Child> records = repository.getRecordsBetween(previousPageNumber, nextPageNumber);
            adapter.addAll(records);
        }
    }
}
