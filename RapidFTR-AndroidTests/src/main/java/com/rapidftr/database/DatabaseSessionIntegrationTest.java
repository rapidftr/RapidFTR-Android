package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import com.rapidftr.activity.BaseActivityIntegrationTest;
import com.rapidftr.activity.LoginActivity;
import lombok.Cleanup;


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

    public void testRawQueryCountAs0() {
        @Cleanup Cursor cursor = session.rawQuery("select count(1) from children", new String[]{});
        cursor.moveToNext();
        assertEquals(cursor.getInt(0), 0);
    }

    public void testInsertNewChildRecord() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DB_CHILD_ID, "id1");
        values.put(DatabaseHelper.DB_CHILD_OWNER, "owner1");
        values.put(DatabaseHelper.DB_CHILD_CONTENT, "content1");

        long id = session.insert("children", null, values);
        assertTrue(id > 0);
    }

    public void testDeleteExistingChildRecord() {
        testInsertNewChildRecord();

        int deleted = session.delete("children", "id = ?", new String[] { "id1" });
        assertTrue(deleted == 1);
    }

    public void testExecSQLDeleteAllFromChildren() {
        testInsertNewChildRecord();
        session.execSQL("DELETE FROM children");
        testRawQueryCountAs0();
    }

    public void testEncryption() {
        try {
            helper = new SQLCipherHelper("test_db", "wrong_password", getActivity());
            fail();
        } catch (Exception e) { }
    }

}
