package com.rapidftr.adapter;

import android.widget.AbsListView;
import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.Repository;
import org.json.JSONException;


public class PaginatedScrollListener<T extends BaseModel> implements AbsListView.OnScrollListener{

    public static final int DEFAULT_PAGE_SIZE = 30;
    private final Repository<T> repository;
    private final HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter;
    private Scroller scroller;

    public PaginatedScrollListener(Repository<T> repository,
                                   HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int numberOfVisibleItems, int numberOfItemsInAdapter) {
        scroller = new Scroller(repository, highlightedFieldsViewAdapter, firstVisibleItem, numberOfVisibleItems, numberOfItemsInAdapter);
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
