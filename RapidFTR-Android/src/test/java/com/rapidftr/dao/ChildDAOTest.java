package com.rapidftr.dao;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.ShadowSQLiteHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(CustomTestRunner.class)
public class ChildDAOTest {

    private ChildDAO dao;

    @Test
    public void testOne() throws IOException {
        dao = new ChildDAO("user1", new ShadowSQLiteHelper());
    }

}
