package com.rapidftr.activity;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class BaseChildActivityTest {
    private EditChildActivity activity;

    @Before
    public void setUp() {
        activity = spy(new EditChildActivity());
    }

    @Test
    public void shouldMarkChildSyncStateToFalseWhenEverChildIsSaved() throws Exception {
        activity.child = new Child("id1", "user1", "{ 'test1' : 'value1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        activity.child.setSynced(true);
        activity.save();
        assertEquals(false, activity.child.isSynced());
    }
}
