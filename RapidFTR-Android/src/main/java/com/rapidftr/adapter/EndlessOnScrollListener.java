package com.rapidftr.adapter;

import android.util.Log;
import android.widget.AbsListView;
import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.Repository;
import org.json.JSONException;
import java.util.List;


public class EndlessOnScrollListener<T extends BaseModel> implements AbsListView.OnScrollListener{

    private Repository<T> repository;
    private HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter;
    private boolean loading = true;
    private int visibleItemThreshold = 5;
    private int currentPage = 0;
    private int numberOfPreviouslyLoadedItems = 0;
    private int previousPageNumber = 0;

    public EndlessOnScrollListener(Repository<T> repository,
                                   HighlightedFieldsViewAdapter<T> highlightedFieldsViewAdapter) {
        this.repository = repository;
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int numberOfVisibleItems, int numberOfItemsInAdapter) {
        if(loading){
            if(numberOfItemsInAdapter > numberOfPreviouslyLoadedItems){
                loading = false;
                numberOfPreviouslyLoadedItems = numberOfItemsInAdapter;
                previousPageNumber = currentPage;
                currentPage+=30;
            }
        }

        if(!loading && (numberOfItemsInAdapter - numberOfVisibleItems) <= (firstVisibleItem + visibleItemThreshold)){
            loading = true;
            try {
                List<T> records = repository.getRecordsForPage(previousPageNumber, currentPage);
                highlightedFieldsViewAdapter.addAll(records);
            } catch (JSONException e) {
                Log.d("PAGINATION", "Failed");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}
}
