package com.rapidftr.repository;

import com.rapidftr.model.Child;
import org.json.JSONException;

import java.util.List;

public class ChildSearch {

    private String searchKey;
    private ChildRepository repository;

    public ChildSearch(String searchKey, ChildRepository repository) {
        this.searchKey = searchKey;
        this.repository = repository;
    }

    public List<Child> getRecordsForNextPage(int currentPageNumber, int nextPageNumber) throws JSONException {
        return repository.getChildrenMatchingStringBetween(searchKey, currentPageNumber, nextPageNumber);
    }
}
