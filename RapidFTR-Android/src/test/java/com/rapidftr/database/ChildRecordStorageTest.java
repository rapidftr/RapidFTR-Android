package com.rapidftr.database;

import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.activity.MainActivity;
import org.junit.Test;

import java.util.List;

public class ChildRecordStorageTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public ChildRecordStorageTest() {
        super("com.rapidftr", MainActivity.class);
    }

    @Test
    public void shouldStoreAndRetrieveChildRecord() {
        ChildRecordStorage storage = new ChildRecordStorage(getActivity(), "some_user", "some_password");
        storage.clearAll();
        storage.addChild("id1", "child1");
        storage.addChild("id2", "child2");
        List<String> children = storage.getAllChildren();
        assertEquals(2, children.size());
        assertEquals("child1", children.get(0));
        assertEquals("child2", children.get(1));
    }

    @Test
    public void shouldNotSeeRecordsFromOtherUsers() {
        ChildRecordStorage storage = new ChildRecordStorage(getActivity(), "user1", "some_password");
        storage.clearAll();

        ChildRecordStorage otherStorage = new ChildRecordStorage(getActivity(), "user2", "some_password");
        otherStorage.clearAll();

        storage.addChild("id3", "child3");
        assertTrue(otherStorage.getAllChildren().isEmpty());
    }

    @Test
    public void shouldClearAllShouldOnlyDeleteRecordsForCurrentUser() {
        ChildRecordStorage storage = new ChildRecordStorage(getActivity(), "user1", "some_password");
        storage.clearAll();

        ChildRecordStorage otherStorage = new ChildRecordStorage(getActivity(), "user2", "some_password");
        otherStorage.clearAll();

        storage.addChild("id5", "child5");
        otherStorage.clearAll();
        assertFalse(storage.getAllChildren().isEmpty());
    }

}
