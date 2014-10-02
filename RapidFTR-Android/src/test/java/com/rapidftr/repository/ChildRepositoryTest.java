package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.FormSectionTest;
import com.rapidftr.model.Child;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import org.hamcrest.CoreMatchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.CustomTestRunner.createUser;
import static com.rapidftr.model.History.*;
import static com.rapidftr.utils.JSONMatcher.equalJSONIgnoreOrder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class ChildRepositoryTest {

    public DatabaseSession session;
    public ChildRepository repository;

    List<FormField> highlightedFormFields;

    @Before
    public void setupSession() throws IOException {
        session = new ShadowSQLiteHelper("test_database").getSession();
        repository = new ChildRepository("user1", session);

        highlightedFormFields = new ArrayList<FormField>();
        List<FormSection> formSections = FormSectionTest.loadFormSectionsFromClassPathResource();
        for (FormSection formSection : formSections) {
            highlightedFormFields.addAll(formSection.getOrderedHighLightedFields());
        }
    }

    @Test
    public void shouldCreateChildRecordAndNotSetLastUpdatedAt() throws JSONException {
        repository.createOrUpdate(new Child("id1", "user1", null));
        assertThat(repository.size(), equalTo(1));
    }

    @Test
    public void shouldCreateChildRecordAndSetCreatedAt() throws Exception {
        repository.createOrUpdate(new Child("id1", "user1", "{ 'test1' : 'value1' }"));
        JSONObject childJsonValues = repository.get("id1").values();
        JSONArray histories = (JSONArray) childJsonValues.get(History.HISTORIES);
        JSONObject changes = (JSONObject) ((JSONObject) histories.get(0)).get("changes");
        assert(((JSONObject) changes.get("child")).has(History.CREATED));
    }

        @Test
    public void shouldUpdateChildRecordIfIdAlreadyExistsAndSetLastUpdateAt() throws Exception {
        ChildRepository repository = spy(new ChildRepository("user1", session));
        repository.createOrUpdate(new Child("id1", "user1", "{ 'test1' : 'value1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }"));

        String updateString = "{ 'test1' : 'value1' }";
        String expectedString = "{'created_by':'user1','test1':'value1','unique_identifier':'id1','last_updated_at':'LAST_UPDATED_AT'}";
        doReturn("LAST_UPDATED_AT").when(repository).getTimeStamp();

        repository.createOrUpdate(new Child("id1", "user1", updateString));
        Child child = repository.get("id1");

        assertThat(child.getUniqueId(), equalTo("id1"));
        JSONObject values = child.values();
        values.remove(History.HISTORIES);
        assertThat(values, equalJSONIgnoreOrder(expectedString));
        assertThat(child.getLastUpdatedAt(), is("LAST_UPDATED_AT"));
    }

    @Test
    public void shouldUpdateChildRecordWithHistory() throws Exception {
        repository.createOrUpdate(new Child("idx", "user1", "{ 'test1' : 'value1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }"));
        Child child = repository.get("idx");
        JSONObject childJsonValues = child.values();
        assertEquals(1, childJsonValues.getJSONArray(History.HISTORIES).length());

        child.put("test1", "value2");
        repository.createOrUpdate(child);
        childJsonValues = repository.get("idx").values();
        assertEquals(2, childJsonValues.getJSONArray(History.HISTORIES).length());
    }

    @Test
    public void shouldSaveInternalIdAndRevDuringChildCreation() throws JSONException {
        Child child1 = new ChildBuilder().withName("tester").withCreatedBy("user1").withUniqueId("abcd1234").build();
        Child child2 = new ChildBuilder().withInternalId("59cd40f39ab6aa791f73885e3bdd99f9").withName("tester").withUniqueId("1234abcd").withRev("4-b011946150a16b0d2c6271aed05e2abe").withCreatedBy("user1").build();
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        HashMap<String, String> allIdsAndRevs = repository.getAllIdsAndRevs();
        assertEquals(2, allIdsAndRevs.size());
        assertEquals("", allIdsAndRevs.get(""));
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdsAndRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));

        child1.put("_id", "dfb2031ebfcbef39dccdb468f5200edc");
        child1.put("_rev", "5-1ed26a0e5072830a9064361a570684f6");
        repository.createOrUpdateWithoutHistory(child1);

        allIdsAndRevs = repository.getAllIdsAndRevs();
        assertEquals(2, allIdsAndRevs.size());
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdsAndRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));
        assertEquals("5-1ed26a0e5072830a9064361a570684f6", allIdsAndRevs.get("dfb2031ebfcbef39dccdb468f5200edc"));

    }

    @Test
    public void shouldGetCorrectlyDeserializesData() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'test1' : 'value1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);

        Child child2 = repository.get("id1");
        assertThat(child1.values(), equalJSONIgnoreOrder(child2.values()));
        assertNotNull(child2.getLastUpdatedAt());
    }

    @Test
    public void shouldCorrectlyGetSyncedState() throws JSONException, IOException {
        Child syncedChild = new Child("syncedID", "user1", null, true);
        Child unsyncedChild = new Child("unsyncedID", "user1", null, false);
        repository.createOrUpdate(syncedChild);
        repository.createOrUpdate(unsyncedChild);

        assertThat(repository.get("syncedID").isSynced(), is(true));
        assertThat(repository.get("unsyncedID").isSynced(), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void getShouldThrowExceptionIfRecordDoesNotExist() throws JSONException {
        repository.get("blah");
    }

    @Test
    public void shouldReturnMatchedChildRecordsBasedOnId() throws Exception {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child3 = new Child("id3", "user3", "{ 'name' : 'child3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ], \"x\": \"y\" }");
        Child child4 = new Child("child1", "user4", "{ 'name' : 'child4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);
        repository.createOrUpdate(child4);

        List<Child> children = repository.getMatchingChildren("hiLd1", null);
        assertEquals(1, children.size());
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

        List<Child> children = repository.getMatchingChildren("child3", highlightedFormFields);
        assertEquals(1, children.size());

        children = repository.getMatchingChildren("hiLd", highlightedFormFields);
        assertEquals(5, children.size());

        children = repository.getMatchingChildren("hiLd1", highlightedFormFields);
        assertEquals(2, children.size());

        children = repository.getMatchingChildren("developer", highlightedFormFields);
        assertEquals(1, children.size());
    }

    @Test
    public void shouldMatchIndependentOfSearchTermOrder() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'john smith', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.getMatchingChildren("first second", highlightedFormFields);
        assertEquals(1, children.size());

        children = repository.getMatchingChildren("second first", highlightedFormFields);
        assertEquals(1, children.size());

        children = repository.getMatchingChildren("sam", highlightedFormFields);
        assertEquals(0, children.size());
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

        List<Child> children = repository.getMatchingChildren("chåld", highlightedFormFields);
        assertEquals(2, children.size());
    }


    @Test
    public void shouldMatchOnlyShortId() throws JSONException, IOException {
        String childId = "abcdefghijklmnop";
        String childShortId = "jklmnop";
        Child child1 = new Child(childId, "user1", "{ 'name' : 'first second', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child1);

        List<Child> children = repository.getMatchingChildren(childId, highlightedFormFields);
        assertEquals(0, children.size());

        children = repository.getMatchingChildren(childShortId, highlightedFormFields);
        assertEquals(1, children.size());
    }


    @Test
    public void shouldNotReturnChildrenCreatedByOtherUnAuthorizedUsers() throws Exception {
        User user1 = createUser("user1");
        user1.setVerified(false);
        User user2 = createUser("user2");
        user2.setVerified(false);
        RapidFtrApplication.getApplicationInstance().setCurrentUser(user1);

        Child child1 = new Child("id1", user1.getUserName(), "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", user2.getUserName(), "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.getMatchingChildren("hild", highlightedFormFields);
        assertEquals(1, children.size());
    }

    @Test
    public void shouldReturnChildRecordsGivenListOfIds() throws Exception {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child3 = new Child("id3", "user3", "{ 'name' : 'child3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ] }");
        Child child4 = new Child("child1", "user4", "{ 'name' : 'child4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);
        repository.createOrUpdate(child4);

        ArrayList<String> listOfIds = new ArrayList<String>();
        listOfIds.add("id1");
        listOfIds.add("id2");
        listOfIds.add("id3");
        listOfIds.add("child1");

        List<Child> children = repository.getChildrenByIds(listOfIds);
        assertEquals(4, children.size());
        assertTrue(children.contains(child1));
        assertTrue(children.contains(child2));
        assertTrue(children.contains(child3));
        assertTrue(children.contains(child4));
    }

    @Test
    public void shouldCorrectlyGetSyncedStateWhenGettingAllRecords() throws JSONException, IOException {
        Child syncedChild = new Child("syncedID", "user1", null, true);
        Child unsyncedChild = new Child("unsyncedID", "user1", null, false);
        repository.createOrUpdate(syncedChild);
        repository.createOrUpdate(unsyncedChild);

        List<Child> all = repository.allCreatedByCurrentUser();
        assertThat(all.get(0).isSynced(), is(true));
        assertThat(all.get(1).isSynced(), is(false));
    }

    @Test
    public void shouldReturnsAllRecords() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{'name':'last_user'}");
        Child child2 = new Child("id2", "user1", "{'name':'first_user'}");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.allCreatedByCurrentUser();
        assertThat(children.size(), equalTo(2));
        assertThat(child2.getInternalId(), equalTo(children.get(0).getInternalId()));
        assertThat(child1.getInternalId(), equalTo(children.get(1).getInternalId()));
    }

    @Test
    public void shouldOnlyReturnsOwnRecordsWhenGettingAll() throws JSONException {
        Child child1 = new Child("id1", "user1", null);
        repository.createOrUpdate(child1);

        ChildRepository anotherUsersRepository = new ChildRepository("user2", session);
        Child child2 = new Child("id2", "user2", null);
        anotherUsersRepository.createOrUpdate(child2);

        assertThat(repository.allCreatedByCurrentUser(), not(hasItem(child2)));
        assertThat(anotherUsersRepository.allCreatedByCurrentUser(), not(hasItem(child1)));
    }

    @Test
    public void shouldReturnAllUnSyncedRecords() throws JSONException {
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user1", null, true);
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.toBeSynced();
        assertThat(children.size(), equalTo(1));
        assertThat(children, hasItems(child1));
    }

    @Test
    public void shouldReturnAllUnSyncedRecordsForGivenUser() throws JSONException {
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user2", null);
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.currentUsersUnsyncedRecords();
        assertThat(children.size(), equalTo(1));
        assertThat(children, hasItems(child1));
    }

    @Test
    public void shouldReturnTrueWhenAChildWithTheGivenIdExistsInTheDatabase() {
        assertThat(repository.exists("1234"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenAChildWithTheGivenIdDoesNotExistInTheDatabase() throws JSONException {
        Child child1 = new Child("iAmARealChild", "user1", null);

        repository.createOrUpdate(child1);

        assertThat(repository.exists("iAmARealChild"), is(true));
    }

    @Test
    public void shouldUpdateAnExstingChild() throws JSONException {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        repository.createOrUpdate(child);
        child.put(Database.ChildTableColumn.owner.getColumnName(), "new owner");
        child.put("someNewField", "someNewValue");

        repository.createOrUpdateWithoutHistory(child);
        Child updatedChild = repository.get("id1");

        assertThat((String) updatedChild.get(Database.ChildTableColumn.owner.getColumnName()), is("new owner"));
        assertThat(updatedChild.get("someNewField").toString(), is("someNewValue"));
    }

    @Test
    public void shouldAddHistoriesIfChildHasBeenUpdated() throws JSONException {
        Child existingChild = new Child("id", "user1", "{'name' : 'oldname'}");
        repository.createOrUpdate(existingChild);
        Child updatedChild = new Child("id", "user1", "{'name' : 'updatedname'}");
        repository.createOrUpdate(updatedChild);

        Child savedChild = repository.get(updatedChild.getUniqueId());
        assertTrue(savedChild.get(HISTORIES).toString().matches(".*\"changes\":\\{.*\"name\":\\{\"to\":\"updatedname\",\"from\":\"oldname\"\\}.*"));
    }

    @Test
    public void shouldReturnChildrenWithTheGivenInternalIds() throws JSONException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, '_id' : 'ae0fc' }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'child2', 'test2' : 0, '_id' : 'b32fa' }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<String> internalIds = new ArrayList<String>();
        internalIds.add("ae0fc");
        internalIds.add("b32fa");

        List<Child> children = repository.getAllWithInternalIds(internalIds);

        assertEquals(2, children.size());
        assertTrue(children.contains(child1));
        assertTrue(children.contains(child2));
    }

    @Test
    public void shouldNotReturnAnyChildGivenInternalIdsDontMatch() throws JSONException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, '_id' : 'ae0fc' }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'child2', 'test2' : 0, '_id' : 'b32fa' }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<String> internalIds = new ArrayList<String>();
        internalIds.add("000fc");
        internalIds.add("1243fa");

        List<Child> children = repository.getAllWithInternalIds(internalIds);

        assertEquals(0, children.size());
    }

    @Test
    public void shouldOnlyReturnChildrenWithMatchingInternalIds() throws JSONException {
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, '_id' : 'ae0fc' }");
        Child child2 = new Child("id2", "user1", "{ 'name' : 'child2', 'test2' : 0, '_id' : 'b32fa' }");
        Child child3 = new Child("id3", "user1", "{ 'name' : 'child3', 'test2' : 0, '_id' : 'c42fa' }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);

        List<String> internalIds = new ArrayList<String>();
        internalIds.add("ae0fc");
        internalIds.add("1443fa");
        internalIds.add("c42fa");

        List<Child> children = repository.getAllWithInternalIds(internalIds);

        assertEquals(2, children.size());
        assertTrue(children.contains(child1));
        assertTrue(children.contains(child3));
    }

    @Test
    public void shouldRetrieveAllIdsAndRevs() throws JSONException {
        Child child1 = new ChildBuilder()
                .withInternalId("dfb2031ebfcbef39dccdb468f5200edc")
                .withName("tester")
                .withRev("5-1ed26a0e5072830a9064361a570684f6")
                .withCreatedBy("user1")
                .withUniqueId("abc123")
                .build();
        Child child2 = new ChildBuilder()
                .withInternalId("59cd40f39ab6aa791f73885e3bdd99f9")
                .withName("tester")
                .withRev("4-b011946150a16b0d2c6271aed05e2abe")
                .withCreatedBy("user1")
                .withUniqueId("bcs234")
                .build();
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        HashMap<String, String> allIdsAndRevs = repository.getAllIdsAndRevs();
        assertEquals(2, allIdsAndRevs.size());
        assertEquals("5-1ed26a0e5072830a9064361a570684f6", allIdsAndRevs.get("dfb2031ebfcbef39dccdb468f5200edc"));
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdsAndRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));

    }

    @Test
    public void shouldDeleteAllRecordOfAGivenUser() throws JSONException {
        Child syncedChild = new Child("syncedID", "user1", null, true);
        Child unSyncedChild = new Child("unsyncedID", "user1", null, false);
        repository.createOrUpdate(syncedChild);
        repository.createOrUpdate(unSyncedChild);

        repository.deleteChildrenByOwner();
        assertEquals(0, repository.allCreatedByCurrentUser().size());
    }

    @Test
    public void shouldCreateNewChildWithoutHistory() throws JSONException {
        Child child = new Child("syncedID", "user1", null, true);

        repository.createOrUpdateWithoutHistory(child);

        Child savedChild = repository.get(child.getUniqueId());
        assertNotNull(savedChild);
        assertFalse(savedChild.has(HISTORIES));
    }

    @Test
    public void shouldUpdateExistingChildWithoutHistory() throws JSONException {
        Child child = new Child("syncedID", "user1", null, true);
        repository.createOrUpdateWithoutHistory(child);
        child.put("more_stuff", "some_more_stuff");
        repository.createOrUpdateWithoutHistory(child);

        Child savedChild = repository.get(child.getUniqueId());

        assertNotNull(savedChild);
        assertFalse(savedChild.has(HISTORIES));
        assertEquals("some_more_stuff", savedChild.get("more_stuff"));
    }

    public class ChildBuilder {
        Child child = new Child();

        public ChildBuilder withName(String name) throws JSONException {
            child.put(Database.ChildTableColumn.name.getColumnName(), name);
            return this;
        }

        public ChildBuilder withInternalId(String id) throws JSONException {
            child.put(Database.ChildTableColumn.internal_id.getColumnName(), id);
            return this;
        }

        public ChildBuilder withRev(String rev) throws JSONException {
            child.put(Database.ChildTableColumn.internal_rev.getColumnName(), rev);
            return this;
        }

        public ChildBuilder withCreatedBy(String createdBy) throws JSONException {
            child.put(Database.ChildTableColumn.created_by.getColumnName(), createdBy);
            return this;
        }

        public ChildBuilder withUniqueId(String uniqueId) throws JSONException {
            child.put(Database.ChildTableColumn.unique_identifier.getColumnName(), uniqueId);
            return this;
        }

        public Child build() {
            return child;
        }

    }

}

