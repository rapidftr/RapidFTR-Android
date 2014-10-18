package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.FormSectionTest;
import com.rapidftr.model.Child;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class ChildSearchTest {

    private ChildRepository repository;
    private DatabaseSession session;
    private ChildSearch childSearch;
    private List<FormField> highlightedFormFields;

    @Before
    public void setUp() throws IOException {
        session = new ShadowSQLiteHelper("test_database").getSession();
        repository = new ChildRepository("user1", session);
        highlightedFormFields = new ArrayList<FormField>();
        List<FormSection> formSections = FormSectionTest.loadFormSectionsFromClassPathResource();
        for (FormSection formSection : formSections) {
            highlightedFormFields.addAll(formSection.getOrderedHighLightedFields());
        }
    }

    @Test
    public void shouldReturnMatchedChildRecordsWithAccentedCharacters() throws JSONException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child3 = new Child("id3", "user3", "{ 'name' : 'chåld3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ], \"x\": \"y\" }");
        Child child4 = new Child("child1", "user4", "{ 'name' : 'chåld4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");
        Child child5 = new Child("child2", "user5", "{ 'name' : 'child4 developer', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);
        repository.createOrUpdate(child4);
        repository.createOrUpdate(child5);

        childSearch = new ChildSearch("chåld", repository, highlightedFormFields);
        List<Child> children = childSearch.getRecordsForFirstPage();
        assertEquals(2, children.size());
    }


    @Test
    public void shouldMatchOnlyShortId() throws JSONException, IOException {
        String childId = "abcdefghijklmnop";
        String childShortId = "jklmnop";
        Child child1 = new Child(childId, "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);
        childSearch = new ChildSearch(childId, repository, highlightedFormFields);

        List<Child> children = childSearch.getRecordsForFirstPage();
        assertEquals(0, children.size());

        childSearch = new ChildSearch(childShortId, repository, highlightedFormFields);
        children = childSearch.getRecordsForFirstPage();
        assertEquals(1, children.size());
    }


    @Test
    public void shouldReturnAllMatchedChildRecordsBasedOnId() throws Exception {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child3 = new Child("id3", "user3", "{ 'name' : 'child3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ], \"x\": \"y\" }");
        Child child4 = new Child("child1", "user4", "{ 'name' : 'child4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);
        repository.createOrUpdate(child4);
        childSearch = new ChildSearch("hiLd1", repository, highlightedFormFields);

        List<Child> children = childSearch.getRecordsForFirstPage();
        assertEquals(2, children.size());
    }

    @Test
    public void shouldReturnMatchedChildRecordsBasedOnHighlightedFields() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child3 = new Child("id3", "user3", "{ 'name' : 'child3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ], \"x\": \"y\" }");
        Child child4 = new Child("child1", "user4", "{ 'name' : 'child4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");
        Child child5 = new Child("child2", "user5", "{ 'name' : 'child4 developer', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);
        repository.createOrUpdate(child4);
        repository.createOrUpdate(child5);

        childSearch = new ChildSearch("child3", repository, highlightedFormFields);
        assertEquals(1, childSearch.getRecordsForFirstPage().size());

        childSearch = new ChildSearch("hiLd", repository, highlightedFormFields);
        assertEquals(5, childSearch.getRecordsForFirstPage().size());

        childSearch = new ChildSearch("hiLd1", repository, highlightedFormFields);
        assertEquals(2, childSearch.getRecordsForFirstPage().size());

        childSearch = new ChildSearch("developer", repository, highlightedFormFields);
        assertEquals(1, childSearch.getRecordsForFirstPage().size());
    }

    @Test
    public void shouldMatchOfSearchTermInCorrectOrder() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'john smith', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        childSearch = new ChildSearch("first second", repository, highlightedFormFields);
        List<Child> children = childSearch.getRecordsForFirstPage();

        assertEquals(1, children.size());
    }

    @Test
    public void shouldReturnAllRecordsMatchingOfSearchTerm() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'john smith', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        childSearch = new ChildSearch("first john", repository, highlightedFormFields);
        List<Child> children = childSearch.getRecordsForFirstPage();

        assertEquals(2, children.size());
    }

    @Test
    public void shouldMatchSearchTermInReverseOrder() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'john smith', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        childSearch = new ChildSearch("second first", repository, highlightedFormFields);
        List<Child> children = childSearch.getRecordsForFirstPage();

        assertEquals(1, children.size());
    }

    @Test
    public void shouldNotMatchDifferentWithSearchTerm() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'john smith', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        childSearch = new ChildSearch("sam", repository, highlightedFormFields);
        List<Child> children = childSearch.getRecordsForFirstPage();

        assertEquals(0, children.size());
    }

}