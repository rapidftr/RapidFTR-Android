package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Child;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(CustomTestRunner.class)
public class ChildRepositoryTest {

    public DatabaseSession session;
    public ChildRepository repository;

    @Before
    public void setupSession() {
        session = new ShadowSQLiteHelper().getSession();
        repository = new ChildRepository("some_user", session);
    }

    @Test
    public void shouldCreateChildRecord() throws JSONException {
        ChildRepository repository = new ChildRepository("some_user", session);
        repository.create(new Child("id1", "some_user", null));
        assertThat(repository.size(), equalTo(1));
    }

    @Test
    public void shouldCreateShouldNotSaveOtherUserRecords() throws Exception {
        ChildRepository repository = new ChildRepository("user1", session);
        try {
            repository.create(new Child("id1", "user2", null));
            throw new Exception();
        } catch (IllegalArgumentException e) { }
    }

    @Test
    public void shouldUpdateChildRecordIfIdAlreadyExists() throws Exception {
        ChildRepository repository = new ChildRepository("user1", session);
        repository.create(new Child("id1", "user1", "{ \"test1\" : \"value1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }"));
        String updateString = "{ \"test1\" : \"value1\" }";
        String expectedString = "{\"created_by\":\"user1\",\"test1\":\"value1\",\"synced\":false,\"_id\":\"id1\"}";
        repository.create(new Child("id1", "user1", updateString));
        Child child = repository.get("id1");
        assertEquals(child.getId(), "id1");
        assertEquals(child.toString(), expectedString);
    }

    @Test
    public void shouldGetCorrectlyDeserializesData() throws JSONException, IOException {
        ChildRepository repository = new ChildRepository("some_user", session);

        Child child1 = new Child("id1", "some_user", "{ \"test1\" : \"value1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }");
        repository.create(child1);

        Child child2 = repository.get("id1");
        assertThat(child1, equalTo(child2));
    }

    @Test
    public void shouldGetOnlyReturnsOwnRecords() throws JSONException {
        ChildRepository repository1 = new ChildRepository("user1", session);
        repository1.create(new Child("id1", "user1", null));

        ChildRepository repository2 = new ChildRepository("user2", session);
        repository2.create(new Child("id2", "user2", null));

        assertThat(repository1.get("id2"), is(nullValue()));
        assertThat(repository2.get("id1"), is(nullValue()));
    }

    @Test
    public void shouldReturnsAllRecords() throws JSONException {
        ChildRepository repository1 = new ChildRepository("user1", session);
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user1", null);
        repository1.create(child1);
        repository1.create(child2);

        List<Child> children = repository1.all();
        assertThat(children.size(), equalTo(2));
        assertThat(children, hasItems(child1, child2));
    }

    @Test
    public void shouldAllOnlyReturnsOwnRecords() throws JSONException {
        ChildRepository repository1 = new ChildRepository("user1", session);
        Child child1 = new Child("id1", "user1", null);
        repository1.create(child1);

        ChildRepository repository2 = new ChildRepository("user2", session);
        Child child2 = new Child("id2", "user2", null);
        repository2.create(child2);

        assertThat(repository1.all(), not(hasItem(child2)));
        assertThat(repository2.all(), not(hasItem(child1)));
    }

    @Test
    public void shouldReturnAllUnSyncedRecords() throws JSONException {
        ChildRepository repository = new ChildRepository("user1", session);
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user1", null, true);
        repository.create(child1);
        repository.create(child2);

        List<Child> children = repository.toBeSynced();
        assertThat(children.size(), equalTo(1));
        assertThat(children, hasItems(child1));
    }

    @Test(expected = JSONException.class)
    public void shouldRaiseRuntTimeExceptionIfTheRequiredChildPropertiesAreNotPopulated() throws RuntimeException, JSONException {
        ChildRepository repository = new ChildRepository("user1", session);
        Child child = new Child();
        assertFalse(child.isSynced());
        repository.create(child);
    }
}
