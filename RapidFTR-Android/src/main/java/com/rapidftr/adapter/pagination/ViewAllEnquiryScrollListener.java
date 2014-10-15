package com.rapidftr.adapter.pagination;

import android.widget.AbsListView;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.json.JSONException;

public class ViewAllEnquiryScrollListener implements AbsListView.OnScrollListener {

    private ViewAllEnquiryScroller scroller;
    private EnquiryRepository repository;
    private HighlightedFieldsViewAdapter<Enquiry> highlightedFieldsViewAdapter;

    public ViewAllEnquiryScrollListener(
            EnquiryRepository repository, HighlightedFieldsViewAdapter<Enquiry> highlightedFieldsViewAdapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
    }

    @Override
    public void onScroll(AbsListView absListView,
                         int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        scroller = new ViewAllEnquiryScroller(repository, highlightedFieldsViewAdapter, firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        scroller.updatePageNumbers();
        try {
            scroller.loadRecordsForNextPage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }
}
