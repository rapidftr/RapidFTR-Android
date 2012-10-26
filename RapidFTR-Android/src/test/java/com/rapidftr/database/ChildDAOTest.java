package com.rapidftr.database;

import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.activity.MainActivity;
import com.rapidftr.model.Child;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class ChildDAOTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private DatabaseHelper helper;

    public ChildDAOTest() {
        super(MainActivity.class);
        helper = new DatabaseHelper("SampleDBKey", getActivity());
    }

    @Test
    public void shouldStoreAndRetrieveChildRecord() throws JSONException {
        ChildDAO storage = new ChildDAO("some_user", helper);
        Child child1 = new Child("id1", "user1", "child1");
        Child child2 = new Child("id2", "user2", "child2");

        storage.clearAll();
        storage.create(child1);
        storage.create(child2);

        List<Child> children = storage.getAllChildren();
        assertEquals(2, children.size());
        assertEquals("child1", children.get(0));
        assertEquals("child2", children.get(1));
    }

    @Test
    public void shouldNotSeeRecordsFromOtherUsers() throws JSONException {
        ChildDAO storage = new ChildDAO("user1", helper);
        storage.clearAll();

        ChildDAO otherStorage = new ChildDAO("user2", helper);
        otherStorage.clearAll();

        storage.create(new Child("id3", "user3", "child3"));
        assertTrue(otherStorage.getAllChildren().isEmpty());
    }

    @Test
    public void shouldClearAllShouldOnlyDeleteRecordsForCurrentUser() throws JSONException {
        ChildDAO storage = new ChildDAO("user1", helper);
        storage.clearAll();

        ChildDAO otherStorage = new ChildDAO("user2", helper);
        otherStorage.clearAll();

        storage.create(new Child("id5", "user5", "child5"));
        otherStorage.clearAll();

        assertFalse(storage.getAllChildren().isEmpty());
    }

}
