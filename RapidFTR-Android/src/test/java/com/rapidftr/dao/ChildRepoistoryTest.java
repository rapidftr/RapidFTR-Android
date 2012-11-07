package com.rapidftr.dao;

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
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(CustomTestRunner.class)
public class ChildRepoistoryTest {

    public DatabaseSession session;
    public ChildRepoistory repoistory;

    @Before
    public void setupSession() {
        session = new ShadowSQLiteHelper().getSession();
        repoistory = new ChildRepoistory("some_user", session);
    }

    @Test
    public void shouldCreateChildRecord() throws JSONException {
        ChildRepoistory repoistory = new ChildRepoistory("some_user", session);
        repoistory.create(new Child("id1", "some_user", null));
        assertThat(repoistory.size(), equalTo(1));
    }

    @Test
    public void shouldCreateShouldNotSaveOtherUserRecords() throws Exception {
        ChildRepoistory repoistory = new ChildRepoistory("user1", session);
        try {
            repoistory.create(new Child("id1", "user2", null));
            throw new Exception();
        } catch (IllegalArgumentException e) { }
    }

    @Test
    public void shouldShouldNotCreateChildRecordIfIDExists() throws Exception {
        ChildRepoistory repoistory = new ChildRepoistory("user1", session);
        repoistory.create(new Child("id1", "user1", null));
        try {
            repoistory.create(new Child("id1", "user1", null));
            throw new Exception();
        } catch (IllegalArgumentException e) { }
    }

    @Test
    public void shouldGetCorrectlyDeserializesData() throws JSONException, IOException {
        ChildRepoistory repoistory = new ChildRepoistory("some_user", session);

        Child child1 = new Child("id1", "some_user", "{ \"test1\" : \"value1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }");
        repoistory.create(child1);

        Child child2 = repoistory.get("id1");
        assertThat(child1, equalTo(child2));
    }

    @Test
    public void shouldGetOnlyReturnsOwnRecords() throws JSONException {
        ChildRepoistory repoistory1 = new ChildRepoistory("user1", session);
        repoistory1.create(new Child("id1", "user1", null));

        ChildRepoistory repoistory2 = new ChildRepoistory("user2", session);
        repoistory2.create(new Child("id2", "user2", null));

        assertThat(repoistory1.get("id2"), is(nullValue()));
        assertThat(repoistory2.get("id1"), is(nullValue()));
    }

    @Test
    public void shouldReturnsAllRecords() throws JSONException {
        ChildRepoistory repoistory1 = new ChildRepoistory("user1", session);
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user1", null);
        repoistory1.create(child1);
        repoistory1.create(child2);

        List<Child> children = repoistory1.all();
        assertThat(children.size(), equalTo(2));
        assertThat(children, hasItems(child1, child2));
    }

    @Test
    public void shouldAllOnlyReturnsOwnRecords() throws JSONException {
        ChildRepoistory repoistory1 = new ChildRepoistory("user1", session);
        Child child1 = new Child("id1", "user1", null);
        repoistory1.create(child1);

        ChildRepoistory repoistory2 = new ChildRepoistory("user2", session);
        Child child2 = new Child("id2", "user2", null);
        repoistory2.create(child2);

        assertThat(repoistory1.all(), not(hasItem(child2)));
        assertThat(repoistory2.all(), not(hasItem(child1)));
    }

    @Test
    public void shouldReturnAllUnSyncedRecords() throws JSONException {
        ChildRepoistory repository = new ChildRepoistory("user1", session);
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user1", null, true);
        repository.create(child1);
        repository.create(child2);

        List<Child> children = repository.toBeSynced();
        assertThat(children.size(), equalTo(1));
        assertThat(children, hasItems(child1));
    }

}
