package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.json.JSONException;

public class ViewAllEnquiryScroller extends Scroller{

    private final EnquiryRepository repository;
    private final HighlightedFieldsViewAdapter<Enquiry> adapter;

    public ViewAllEnquiryScroller(EnquiryRepository repository, HighlightedFieldsViewAdapter<Enquiry> adapter,
                                  int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        super(firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        this.repository = repository;
        this.adapter = adapter;
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if(shouldQueryForMoreData()){
            loading = true;
            adapter.addAll(repository.getRecordsBetween(previousPageNumber, nextPageNumber));
        }
    }
}
