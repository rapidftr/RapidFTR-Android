package com.rapidftr.adapter.pagination;

import android.widget.AbsListView;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;


public class ViewAllChildrenPaginatedScrollListener implements AbsListView.OnScrollListener{

    public static final int DEFAULT_PAGE_SIZE = 30;
    public static final int FIRST_PAGE = 30;
    private ViewAllChildScroller scroller;

    public ViewAllChildrenPaginatedScrollListener(ChildRepository repository,
                                                  HighlightedFieldsViewAdapter<Child> adapter) {
        scroller = new ViewAllChildScroller(repository, adapter);
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int numberOfVisibleItems, int numberOfItemsInAdapter) {
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
