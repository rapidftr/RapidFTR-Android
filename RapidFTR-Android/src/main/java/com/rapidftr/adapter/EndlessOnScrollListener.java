package com.rapidftr.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.Toast;
import com.rapidftr.RapidFtrApplication;
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
                highlightedFieldsViewAdapter.removeFirstPage();
                numberOfPreviouslyLoadedItems = numberOfItemsInAdapter;
                previousPageNumber = currentPage;
                currentPage+=30;
            }
        }

        if(!loading && (numberOfItemsInAdapter - numberOfVisibleItems) <= (firstVisibleItem + visibleItemThreshold)){
            loading = true;
            try {
                List<T> records = repository.getRecordsForPage(previousPageNumber, currentPage);
                Context applicationContext = RapidFtrApplication.getApplicationInstance().getApplicationContext();
                int count = highlightedFieldsViewAdapter.getCount();
                Toast.makeText(applicationContext, String.format("Record count: %d", count), Toast.LENGTH_LONG).show();
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
