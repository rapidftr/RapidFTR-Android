package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.activity.LoginActivity;
import lombok.Cleanup;
import org.junit.Ignore;

@Ignore
public class DatabaseSessionIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private DatabaseHelper dbHelper;

    public DatabaseSessionIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        dbHelper = new SQLCipherHelper("SampleDBKey", getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        @Cleanup DatabaseSession SQLite = dbHelper.openSession();
        SQLite.execSQL("DROP TABLE CHILDREN");
        dbHelper.close();
    }

    public void testCreateTableAndAccessIt() throws Exception {
        @Cleanup DatabaseSession SQLite = dbHelper.openSession();
        SQLite.execSQL("CREATE TABLE CHILDREN (CHILD_NAME VARCHAR(255),CHILD_ID VARCHAR(255))");

        ContentValues values = new ContentValues();
        values.put("CHILD_NAME", "child name");
        values.put("CHILD_ID", "1234");
        SQLite.insert("CHILDREN", null, values);

        Cursor cursor = SQLite.rawQuery("SELECT CHILD_NAME FROM CHILDREN WHERE CHILD_ID=?", new String[]{"1234"});
        while (cursor.moveToNext()) {
            assertEquals(cursor.getString(0), "child name");
        }
    }

}
