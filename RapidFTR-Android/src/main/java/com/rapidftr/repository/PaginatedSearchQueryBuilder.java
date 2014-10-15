package com.rapidftr.repository;

import com.rapidftr.RapidFtrApplication;
import org.json.JSONException;

public class PaginatedSearchQueryBuilder {

    private final RapidFtrApplication applicationContext;
    private final String searchKey;

    public PaginatedSearchQueryBuilder(RapidFtrApplication applicationContext, String searchKey) {
        this.applicationContext = applicationContext;
        this.searchKey = searchKey;
    }

    public String queryForMatchingChildrenFirstPage() throws JSONException {
        StringBuilder queryBuilder = buildQuery();
        return queryBuilder.append(") LIMIT 30").toString();
    }

    public String queryForMatchingChildrenBetweenPages(int fromPageNumber, int toPageNumber) throws JSONException {
        StringBuilder queryBuilder = buildQuery();
        return queryBuilder.append(String.format(") LIMIT %d, %d", fromPageNumber, toPageNumber)).toString();
    }

    private StringBuilder buildQuery() throws JSONException {
        StringBuilder queryBuilder = new StringBuilder("SELECT child_json, synced FROM children WHERE (").append(fetchByOwner());
        String[] subQueries = searchKey.split("\\s+");
        queryBuilder.append(subQueries(subQueries));
        return queryBuilder;
    }

    private String subQueries(String[] subQueries) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < subQueries.length; i++) {
            builder.append(String.format("child_json LIKE '%%%s%%' OR id LIKE '%%%s%%'", subQueries[i], subQueries[i]));
            if (i < subQueries.length - 1) {
                builder.append(" OR ");
            }
        }
        return builder.toString();
    }

    private String fetchByOwner() throws JSONException {
        if (!applicationContext.getCurrentUser().isVerified()) {
            return " child_owner = '" + applicationContext.getCurrentUser().getUserName() + "' AND ";
        } else {
            return "";
        }
    }
}
