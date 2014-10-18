package com.rapidftr.repository;

import com.rapidftr.forms.FormField;
import com.rapidftr.model.Child;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChildSearch {

    private final String searchKey;
    private final ChildRepository repository;
    private final List<FormField> highlightedFields;
    private Pattern pattern;

    public ChildSearch(String searchKey, ChildRepository repository, List<FormField> highlightedFields) {
        this.searchKey = searchKey;
        this.repository = repository;
        this.highlightedFields = highlightedFields;
        this.pattern = buildPatternFromSearchString(searchKey);
    }

    public List<Child> getRecordsForFirstPage() throws JSONException {
        List<Child> children = repository.getFirstPageOfChildrenMatchingString(searchKey);
        return filterChildrenWithRegularExpression(children, highlightedFields);
    }

    public List<Child> getRecordsForNextPage(int currentPageNumber, int nextPageNumber) throws JSONException {
        List<Child> children = repository.getChildrenMatchingStringBetween(searchKey, currentPageNumber, nextPageNumber);
        return filterChildrenWithRegularExpression(children, highlightedFields);
    }

    private List<Child> filterChildrenWithRegularExpression(List<Child> childRecords,
                                                            List<FormField> highlightedFields) throws JSONException {
        List<Child> children = new ArrayList<Child>();

        for(Child child: childRecords){
            if (pattern.matcher(child.getShortId()).matches()) {
                children.add(child);
            } else {
                for (FormField formField : highlightedFields) {
                    boolean formFieldMatchesPattern = pattern.matcher(child.optString(formField.getId())).matches();
                    if (!children.contains(child) && formFieldMatchesPattern) {
                        children.add(child);
                        break;
                    }
                }
            }
        }
        return children;
    }

    private Pattern buildPatternFromSearchString(String searchString) {
        String[] splitQuery = searchString.split("\\s+");
        StringBuilder regexBuilder = new StringBuilder();
        for (int i = 0; i < splitQuery.length; i++) {
            regexBuilder.append(String.format(".*(%s)+.*", splitQuery[i]));
            if ((i < splitQuery.length - 1)) {
                regexBuilder.append("|");
            }
        }
        return Pattern.compile(regexBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }
}
