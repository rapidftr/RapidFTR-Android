package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.Repository;
import org.json.JSONException;

import java.util.List;

public class ViewAllChildScroller extends Scroller<Child> {

    public ViewAllChildScroller(Repository<Child> repository, HighlightedFieldsViewAdapter<Child> adapter,
                                int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        super(repository, adapter, firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if (shouldQueryForMoreData()) {
            loading = true;
            List<Child> records = repository.getRecordsBetween(previousPageNumber, nextPageNumber);
            highlightedFieldsViewAdapter.addAll(records);
        }

    }
}
