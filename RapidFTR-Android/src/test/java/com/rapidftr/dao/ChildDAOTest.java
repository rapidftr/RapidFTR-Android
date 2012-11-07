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
public class ChildDAOTest {

    public DatabaseSession session;
    public ChildDAO dao;

    @Before
    public void setupSession() {
        session = new ShadowSQLiteHelper().getSession();
        dao = new ChildDAO("some_user", session);
    }

    @Test
    public void testCreateChildRecord() throws JSONException {
        ChildDAO dao = new ChildDAO("some_user", session);
        dao.create(new Child("id1", "some_user", null));
        assertThat(dao.size(), equalTo(1));
    }

    @Test
    public void testCreateShouldNotSaveOtherUserRecords() throws Exception {
        ChildDAO dao = new ChildDAO("user1", session);
        try {
            dao.create(new Child("id1", "user2", null));
            throw new Exception();
        } catch (IllegalArgumentException e) { }
    }

    @Test
    public void testShouldNotCreateChildRecordIfIDExists() throws Exception {
        ChildDAO dao = new ChildDAO("user1", session);
        dao.create(new Child("id1", "user1", null));
        try {
            dao.create(new Child("id1", "user1", null));
            throw new Exception();
        } catch (IllegalArgumentException e) { }
    }

    @Test
    public void testGetCorrectlyDeserializesData() throws JSONException, IOException {
        ChildDAO dao = new ChildDAO("some_user", session);

        Child child1 = new Child("id1", "some_user", "{ \"test1\" : \"value1\", \"test2\" : 0, \"test3\" : [ \"1\", 2, \"3\" ] }");
        dao.create(child1);

        Child child2 = dao.get("id1");
        assertThat(child1, equalTo(child2));
    }

    @Test
    public void testGetOnlyReturnsOwnRecords() throws JSONException {
        ChildDAO dao1 = new ChildDAO("user1", session);
        dao1.create(new Child("id1", "user1", null));

        ChildDAO dao2 = new ChildDAO("user2", session);
        dao2.create(new Child("id2", "user2", null));

        assertThat(dao1.get("id2"), is(nullValue()));
        assertThat(dao2.get("id1"), is(nullValue()));
    }

    @Test
    public void testReturnsAllRecords() throws JSONException {
        ChildDAO dao1 = new ChildDAO("user1", session);
        Child child1 = new Child("id1", "user1", null);
        Child child2 = new Child("id2", "user1", null);
        dao1.create(child1);
        dao1.create(child2);

        List<Child> children = dao1.all();
        assertThat(children.size(), equalTo(2));
        assertThat(children, hasItems(child1, child2));
    }

    @Test
    public void testAllOnlyReturnsOwnRecords() throws JSONException {
        ChildDAO dao1 = new ChildDAO("user1", session);
        Child child1 = new Child("id1", "user1", null);
        dao1.create(child1);

        ChildDAO dao2 = new ChildDAO("user2", session);
        Child child2 = new Child("id2", "user2", null);
        dao2.create(child2);

        assertThat(dao1.all(), not(hasItem(child2)));
        assertThat(dao2.all(), not(hasItem(child1)));
    }

}
