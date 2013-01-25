package com.rapidftr.repository;

import android.content.SharedPreferences;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.rapidftr.CustomTestRunner.createUser;
import static com.rapidftr.model.Child.History.*;
import static com.rapidftr.utils.JSONMatcher.equalJSONIgnoreOrder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class ChildRepositoryTest {

    public DatabaseSession session;
    public ChildRepository repository;

    @Before
    public void setupSession() {
        session = new ShadowSQLiteHelper().getSession();
        repository = new ChildRepository("user1", session);
    }

    @Test
    public void shouldCreateChildRecordAndNotSetLastUpdatedAt() throws JSONException {
        repository.createOrUpdate(new Child("id1", "user1", null));
        assertThat(repository.size(), equalTo(1));
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
        assertThat(child.values(), equalJSONIgnoreOrder(expectedString));
        assertThat(child.getLastUpdatedAt(), is("LAST_UPDATED_AT"));
    }

    @Test
    public void shouldSaveInternalIdAndRevDuringChildCreation() throws JSONException {
        Child child1 = new ChildBuilder().withName("tester").withCreatedBy("user1").withUniqueId("abcd1234").build();
        Child child2 = new ChildBuilder().withId("59cd40f39ab6aa791f73885e3bdd99f9").withName("tester").withUniqueId("1234abcd").withRev("4-b011946150a16b0d2c6271aed05e2abe").withCreatedBy("user1").build();
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        HashMap<String, String> allIdsAndRevs = repository.getAllIdsAndRevs();
        assertEquals(2, allIdsAndRevs.size());
        assertEquals("", allIdsAndRevs.get(""));
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdsAndRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));

        child1.put("_id", "dfb2031ebfcbef39dccdb468f5200edc");
        child1.put("_rev", "5-1ed26a0e5072830a9064361a570684f6");
        repository.update(child1);

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
    public void shouldReturnMatchedChildRecords() throws Exception{
        Child child1 = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child3 = new Child("id3", "user3", "{ 'name' : 'child3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ] }");
        Child child4 = new Child("child1", "user4", "{ 'name' : 'child4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);
        repository.createOrUpdate(child3);
        repository.createOrUpdate(child4);

        List<Child> children = repository.getMatchingChildren("hiLd1");
        assertEquals(2, children.size());
        assertThat(child1, equalTo(children.get(0)));
        assertThat(child4, equalTo(children.get(1)));
    }

    @Test
    public void shouldNotReturnChildrenCreatedByOtherUnAuthorizedUsers() throws Exception {
        User user1 = createUser("user1");
	    user1.setAuthenticated(false);
        User user2 = createUser("user2");
	    user2.setAuthenticated(false);
	    RapidFtrApplication.getApplicationInstance().setCurrentUser(user1);

        Child child1 = new Child("id1", user1.getUserName(), "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        Child child2 = new Child("id2", user2.getUserName(), "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");

        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.getMatchingChildren("hiLd");
        assertEquals(1, children.size());
    }
    
    @Test
    public void shouldCorrectlyGetSyncedStateWhenGettingAllRecords() throws JSONException, IOException {
        Child syncedChild = new Child("syncedID", "user1", null, true);
        Child unsyncedChild = new Child("unsyncedID", "user1", null, false);
        repository.createOrUpdate(syncedChild);
        repository.createOrUpdate(unsyncedChild);

        List<Child> all = repository.getChildrenByOwner();
        assertThat(all.get(0).isSynced(), is(true));
        assertThat(all.get(1).isSynced(), is(false));
    }

    @Test
    public void shouldReturnsAllRecords() throws JSONException, IOException {
        Child child1 = new Child("id1", "user1", "{'name':'last_user'}");
        Child child2 = new Child("id2", "user1", "{'name':'first_user'}");
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        List<Child> children = repository.getChildrenByOwner();
        assertThat(children.size(), equalTo(2));
        assertThat(child2, equalTo(children.get(0)));
        assertThat(child1, equalTo(children.get(1)));
    }

    @Test
    public void shouldOnlyReturnsOwnRecordsWhenGettingAll() throws JSONException {
        Child child1 = new Child("id1", "user1", null);
        repository.createOrUpdate(child1);

        ChildRepository anotherUsersRepository = new ChildRepository("user2", session);
        Child child2 = new Child("id2", "user2", null);
        anotherUsersRepository.createOrUpdate(child2);

        assertThat(repository.getChildrenByOwner(), not(hasItem(child2)));
        assertThat(anotherUsersRepository.getChildrenByOwner(), not(hasItem(child1)));
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
        child.put("someNewField", "someNewValue");

        repository.update(child);
        Child updatedChild = repository.get("id1");

        assertThat(updatedChild.get("someNewField").toString(), is("someNewValue"));
    }

    @Test
    public void shouldAddHistoriesIfChildHasBeenUpdated() throws JSONException {
        Child existingChild = new Child("id", "user1", "{'name' : 'old-name'}");
        repository.createOrUpdate(existingChild);

        Child updatedChild = new Child("id", "user1", "{'name' : 'updated-name'}");
        Child spyUpdatedChild = spy(updatedChild);
        List<Child.History> histories = new ArrayList<Child.History>();
        Child.History history = updatedChild.new History();
        HashMap changes = new HashMap();
        HashMap fromTo = new LinkedHashMap();
        fromTo.put(FROM, "old-name");
        fromTo.put(TO, "new-name");
        changes.put("name", fromTo);
        history.put(USER_NAME, "user");
        history.put(DATETIME, "timestamp");
        history.put(CHANGES, changes);
        histories.add(history);

        doReturn(histories).when(spyUpdatedChild).changeLogs(existingChild);
        repository.createOrUpdate(spyUpdatedChild);

        verify(spyUpdatedChild).put(HISTORIES, "[{\"user_name\":\"user\",\"datetime\":\"timestamp\",\"changes\":{\"name\":{\"from\":\"old-name\",\"to\":\"new-name\"}}}]");
        Child savedChild = repository.get(updatedChild.getUniqueId());
        assertThat(savedChild.get(HISTORIES).toString(), is("[{\"user_name\":\"user\",\"datetime\":\"timestamp\",\"changes\":{\"name\":{\"to\":\"new-name\",\"from\":\"old-name\"}}}]"));
    }

    @Test
    public void shouldAppendHistoryIfHistoriesAlreadyExist() throws JSONException {
        Child existingChild = new Child("id", "user1", "{\"name\":\"old-name\",\"histories\":[{\"changes\":{\"name\":{}}}, {\"changes\":{\"sex\":{}}}]}");
        repository.createOrUpdate(existingChild);

        Child updatedChild = new Child("id", "user1", "{'name' : 'updated-name'}");
        Child spyUpdatedChild = spy(updatedChild);
        List<Child.History> histories = new ArrayList<Child.History>();
        Child.History history = updatedChild.new History();
        HashMap changes = new HashMap();
        HashMap fromTo = new LinkedHashMap();
        fromTo.put(FROM, "old-name");
        fromTo.put(TO, "new-name");
        changes.put("name", fromTo);
        history.put(USER_NAME, "user");
        history.put(DATETIME, "timestamp");
        history.put(CHANGES, changes);
        histories.add(history);

        doReturn(histories).when(spyUpdatedChild).changeLogs(existingChild);
        repository.createOrUpdate(spyUpdatedChild);

        verify(spyUpdatedChild).put(HISTORIES, "[{\"changes\":{\"name\":{}}},{\"changes\":{\"sex\":{}}},{\"user_name\":\"user\",\"datetime\":\"timestamp\",\"changes\":{\"name\":{\"from\":\"old-name\",\"to\":\"new-name\"}}}]");
    }

    @Test
    public void shouldConstructTheHistoryObjectIfHistoriesArePassedAsStringInContent() throws JSONException {
        Child child = new Child("id", "user1", "{\"histories\":[{\"changes\":{\"name\":{\"from\":\"old-name\",\"to\":\"new-name\"}}}, {\"changes\":{\"sex\":{\"from\":\"\",\"to\":\"male\"}}}]}", false);
        repository.createOrUpdate(child);
        List<Child> children = repository.toBeSynced();
        JSONArray histories = (JSONArray) children.get(0).get(HISTORIES);
        assertThat(histories.length(), is(2));
        JSONObject name = (JSONObject) ((JSONObject) ((JSONObject) histories.get(0)).get("changes")).get("name");
        JSONObject sex = (JSONObject) ((JSONObject) ((JSONObject) histories.get(1)).get("changes")).get("sex");
        assertThat(name.get("from").toString(), is("old-name"));
        assertThat(name.get("to").toString(), is("new-name"));
        assertThat(sex.get("from").toString(), is(""));
        assertThat(sex.get("to").toString(), is("male"));
    }

    @Test
    public void shouldRetrieveAllIdsAndRevs() throws JSONException {
        Child child1 = new ChildBuilder().withId("dfb2031ebfcbef39dccdb468f5200edc").withName("tester").withRev("5-1ed26a0e5072830a9064361a570684f6").withCreatedBy("user1").build();
        Child child2 = new ChildBuilder().withId("59cd40f39ab6aa791f73885e3bdd99f9").withName("tester").withRev("4-b011946150a16b0d2c6271aed05e2abe").withCreatedBy("user1").build();
        repository.createOrUpdate(child1);
        repository.createOrUpdate(child2);

        HashMap<String, String> allIdsAndRevs = repository.getAllIdsAndRevs();
        assertEquals(2, allIdsAndRevs.size());
        assertEquals("5-1ed26a0e5072830a9064361a570684f6", allIdsAndRevs.get("dfb2031ebfcbef39dccdb468f5200edc"));
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdsAndRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));

    }

    public class ChildBuilder {
        Child child = new Child();

        public ChildBuilder withName(String name) throws JSONException {
            child.put("name", name);
            return this;
        }

        public ChildBuilder withId(String id) throws JSONException {
            child.put("_id", id);
            return this;
        }

        public ChildBuilder withRev(String rev) throws JSONException {
            child.put("_rev", rev);
            return this;
        }

        public ChildBuilder withCreatedBy(String createdBy) throws JSONException {
            child.put("created_by", createdBy);
            return this;
        }

        public ChildBuilder withUniqueId(String uniqueId) throws JSONException {
            child.put("unique_identifier", uniqueId);
            return this;
        }
        public Child build() {
            return child;
        }

    }

}

