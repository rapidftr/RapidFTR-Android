package com.rapidftr.adapter.pagination;

import android.widget.AbsListView;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.EnquiryRepository;
import org.json.JSONException;

public class ViewAllEnquiryScrollListener implements AbsListView.OnScrollListener {

    private ViewAllEnquiryScroller scroller;

    public ViewAllEnquiryScrollListener(
            EnquiryRepository repository, HighlightedFieldsViewAdapter<Enquiry> highlightedFieldsViewAdapter) {
        scroller = new ViewAllEnquiryScroller(repository, highlightedFieldsViewAdapter);
    }

    @Override
    public void onScroll(AbsListView absListView,
                         int firstVisibleItem, int numberOfVisibleItems, int numberOfItemsInAdapter) {
        scroller.updateRecordNumbers( firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        try {
            scroller.loadRecordsForNextPage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}
}
