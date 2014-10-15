package com.rapidftr.adapter.pagination;

import android.widget.AbsListView;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import lombok.Cleanup;
import org.json.JSONException;


public class ViewAllChildrenPaginatedScrollListener implements AbsListView.OnScrollListener{

    public static final int DEFAULT_PAGE_SIZE = 30;
    public static final int FIRST_PAGE = 30;
    private final ChildRepository repository;
    private final HighlightedFieldsViewAdapter<Child> highlightedFieldsViewAdapter;
    private ViewAllChildScroller scroller;

    public ViewAllChildrenPaginatedScrollListener(ChildRepository repository,
                                                  HighlightedFieldsViewAdapter<Child> adapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = adapter;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int numberOfVisibleItems, int numberOfItemsInAdapter) {
        scroller = new ViewAllChildScroller(repository, highlightedFieldsViewAdapter, firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
        scroller.updatePageNumbers();

        try {
            scroller.loadRecordsForNextPage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}
}
