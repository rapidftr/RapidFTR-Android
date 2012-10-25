package com.rapidftr.database;

import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.activity.MainActivity;
import com.rapidftr.model.Child;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class ChildRecordStorageTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public ChildRecordStorageTest() {
        super(MainActivity.class);
    }

    @Test
    public void shouldStoreAndRetrieveChildRecord() throws JSONException {
        ChildRecordStorage storage = new ChildRecordStorage("some_user", "some_password");
        Child child1 = new Child("id1", "user1", "child1");
        Child child2 = new Child("id2", "user2", "child2");

        storage.clearAll();
        storage.addChild(child1);
        storage.addChild(child2);

        List<Child> children = storage.getAllChildren();
        assertEquals(2, children.size());
        assertEquals("child1", children.get(0));
        assertEquals("child2", children.get(1));
    }

    @Test
    public void shouldNotSeeRecordsFromOtherUsers() throws JSONException {
        ChildRecordStorage storage = new ChildRecordStorage("user1", "some_password");
        storage.clearAll();

        ChildRecordStorage otherStorage = new ChildRecordStorage("user2", "some_password");
        otherStorage.clearAll();

        storage.addChild(new Child("id3", "user3", "child3"));
        assertTrue(otherStorage.getAllChildren().isEmpty());
    }

    @Test
    public void shouldClearAllShouldOnlyDeleteRecordsForCurrentUser() throws JSONException {
        ChildRecordStorage storage = new ChildRecordStorage("user1", "some_password");
        storage.clearAll();

        ChildRecordStorage otherStorage = new ChildRecordStorage("user2", "some_password");
        otherStorage.clearAll();

        storage.addChild(new Child("id5", "user5", "child5"));
        otherStorage.clearAll();

        assertFalse(storage.getAllChildren().isEmpty());
    }

}
