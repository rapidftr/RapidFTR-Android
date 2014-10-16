package com.rapidftr.adapter.pagination;

import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.json.JSONException;
import static com.rapidftr.adapter.pagination.ViewAllChildrenPaginatedScrollListener.DEFAULT_PAGE_SIZE;


public class ViewAllEnquiryScroller extends Scroller{

    private final EnquiryRepository repository;
    private final HighlightedFieldsViewAdapter<Enquiry> adapter;

    public ViewAllEnquiryScroller(EnquiryRepository repository, HighlightedFieldsViewAdapter<Enquiry> adapter) {
        super();
        this.repository = repository;
        this.adapter = adapter;
    }

    @Override
    public void loadRecordsForNextPage() throws JSONException {
        if(shouldQueryForMoreData()){
            adapter.addAll(repository.getRecordsBetween(adapter.getCount(), adapter.getCount() + DEFAULT_PAGE_SIZE));
        }
    }
}
