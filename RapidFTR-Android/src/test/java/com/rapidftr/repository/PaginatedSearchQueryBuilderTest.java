package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class PaginatedSearchQueryBuilderTest {

    private User user;
    private RapidFtrApplication applicationContext;
    private PaginatedSearchQueryBuilder queryBuilder;

    @Before
    public void setUp() throws Exception {
        user = spy(new User("user1"));
        applicationContext = spy(RapidFtrApplication.getApplicationInstance());
        doReturn(user).when(applicationContext).getCurrentUser();
    }

    @Test
    public void shouldReturnFirstThirtyMatchesForCurrentUserGivenUnverifiedUser() throws JSONException {
        doReturn(false).when(user).isVerified();
        queryBuilder = new PaginatedSearchQueryBuilder(applicationContext, "john");

        String actualQuery = queryBuilder.queryForMatchingChildrenFirstPage();

        String expectQuery = "SELECT child_json, synced " +
                "FROM children WHERE ( child_owner = 'user1' AND child_json LIKE '%john%' OR id LIKE '%john%') LIMIT 30";
        assertEquals(expectQuery, actualQuery);
    }

    @Test
    public void shouldReturnQueryWithLimitForFirstThirtyMatchesGivenVerifiedUser() throws JSONException {
        doReturn(true).when(user).isVerified();
        PaginatedSearchQueryBuilder queryBuilder = new PaginatedSearchQueryBuilder(applicationContext, "john");

        String actualQuery = queryBuilder.queryForMatchingChildrenFirstPage();

        String expectQuery = "SELECT child_json, synced " +
                "FROM children WHERE (child_json LIKE '%john%' OR id LIKE '%john%') LIMIT 30";
        assertEquals(expectQuery, actualQuery);
    }

    @Test
    public void shouldReturnQueryForFirstThirtyMatchesWithAllTwoSubQueries() throws JSONException {
        doReturn(true).when(user).isVerified();
        PaginatedSearchQueryBuilder queryBuilder = new PaginatedSearchQueryBuilder(applicationContext, "john doe");

        String actualQuery = queryBuilder.queryForMatchingChildrenFirstPage();

        String expectQuery = "SELECT child_json, synced " +
                "FROM children WHERE (child_json LIKE '%john%' OR id LIKE '%john%' " +
                "OR child_json LIKE '%doe%' OR id LIKE '%doe%') LIMIT 30";
        assertEquals(expectQuery, actualQuery);
    }

    @Test
    public void shouldReturnQueryForFirstThirtyMatchesWithMoreThanTwoSubQueries() throws JSONException {
        doReturn(true).when(user).isVerified();
        PaginatedSearchQueryBuilder queryBuilder = new PaginatedSearchQueryBuilder(applicationContext, "john doe foo");

        String actualQuery = queryBuilder.queryForMatchingChildrenFirstPage();

        String expectQuery = "SELECT child_json, synced " +
                "FROM children WHERE (child_json LIKE '%john%' OR id LIKE '%john%' " +
                "OR child_json LIKE '%doe%' OR id LIKE '%doe%' " +
                "OR child_json LIKE '%foo%' OR id LIKE '%foo%') LIMIT 30";
        assertEquals(expectQuery, actualQuery);
    }

    @Test
    public void returnsQueryForMatchesBetweenSpecifiedPageNumbers() throws JSONException {
        doReturn(true).when(user).isVerified();
        PaginatedSearchQueryBuilder queryBuilder = new PaginatedSearchQueryBuilder(applicationContext, "john doe foo");

        String actualQuery = queryBuilder.queryForMatchingChildrenBetweenPages(1, 10);

        String expectQuery = "SELECT child_json, synced " +
                "FROM children WHERE (child_json LIKE '%john%' OR id LIKE '%john%' " +
                "OR child_json LIKE '%doe%' OR id LIKE '%doe%' " +
                "OR child_json LIKE '%foo%' OR id LIKE '%foo%') LIMIT 1, 10";
        assertEquals(expectQuery, actualQuery);
    }

}
