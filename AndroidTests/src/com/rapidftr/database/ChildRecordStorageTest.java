package com.rapidftr.database;

import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.activity.MainActivity;

import java.util.List;

public class ChildRecordStorageTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public ChildRecordStorageTest() {
        super("com.rapidftr", MainActivity.class);
    }

    public void testShouldStoreAndRetrieveChildRecord() {
        ChildRecordStorage storage = new ChildRecordStorage(getActivity(), "some_user", "some_password");
        storage.clearAll();
        storage.addChild("id1", "child1");
        storage.addChild("id2", "child2");
        List<String> children = storage.getAllChildren();
        assertEquals(2, children.size());
        assertEquals("child1", children.get(0));
        assertEquals("child2", children.get(1));
    }

    public void testShouldNotSeeRecordsFromOtherUsers() {
        ChildRecordStorage storage = new ChildRecordStorage(getActivity(), "user1", "some_password");
        storage.clearAll();

        ChildRecordStorage otherStorage = new ChildRecordStorage(getActivity(), "user2", "some_password");
        otherStorage.clearAll();

        storage.addChild("id3", "child3");
        assertTrue(otherStorage.getAllChildren().isEmpty());
    }

    public void testClearAllShouldOnlyDeleteRecordsForCurrentUser() {
        ChildRecordStorage storage = new ChildRecordStorage(getActivity(), "user1", "some_password");
        storage.clearAll();

        ChildRecordStorage otherStorage = new ChildRecordStorage(getActivity(), "user2", "some_password");
        otherStorage.clearAll();

        storage.addChild("id5", "child5");
        otherStorage.clearAll();
        assertFalse(storage.getAllChildren().isEmpty());
    }

}
