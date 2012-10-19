package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.LoginActivity;
import org.junit.Ignore;

@Ignore
public class DatabaseSessionIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private DatabaseHelper dbHelper;

    public DatabaseSessionIntegrationTest() {
        super("com.rapidftr", LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        dbHelper = new DatabaseHelper();
        RapidFtrApplication.setDbKey("DB_KEY");
    }

    @Override
    public void tearDown() throws Exception {
        DatabaseSession session = new DatabaseHelper().getSession();
        session.execute("DROP TABLE CHILDREN");
    }

    public void testCreateTableAndAccessIt(){
        DatabaseSession session = dbHelper.getSession();
        session.execute("CREATE TABLE CHILDREN (CHILD_NAME VARCHAR(255),CHILD_ID VARCHAR(255))");

        session = dbHelper.getSession();
        ContentValues values = new ContentValues();
        values.put("CHILD_NAME", "child name");
        values.put("CHILD_ID", "1234");
        session.insert("CHILDREN", values);

        session = dbHelper.getSession();
        Cursor cursor = session.rawQuery("SELECT CHILD_NAME FROM CHILDREN WHERE CHILD_ID=?", new String[]{"1234"});
        while (cursor.moveToNext()) {
            assertEquals(cursor.getString(0), "child name");
        }
    }
}