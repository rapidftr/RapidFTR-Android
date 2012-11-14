package com.rapidftr.database;

import com.rapidftr.activity.BaseActivityIntegrationTest;
import com.rapidftr.activity.LoginActivity;
import org.junit.Test;


public class DatabaseSessionIntegrationTest extends BaseActivityIntegrationTest<LoginActivity> {

    public DatabaseHelper helper;
    public DatabaseSession session;

    public DatabaseSessionIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        helper = new SQLCipherHelper("test_db", "test_key", getActivity());
        session = helper.getSession();
        session.execSQL("DELETE FROM children");
    }

    @Override
    public void tearDown() throws Exception {
        session.close();
        helper.close();
        super.tearDown();
    }

    @Test(expected = Exception.class)
    public void shouldNotBeAbleToAccessDatabaseWithIncorrectDecrypitionKey() {
        helper = new SQLCipherHelper("test_db", "wrong_password", getActivity());
    }

}
