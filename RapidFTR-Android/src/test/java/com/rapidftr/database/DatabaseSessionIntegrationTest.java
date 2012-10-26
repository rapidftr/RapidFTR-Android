package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import com.rapidftr.activity.LoginActivity;
import lombok.Cleanup;
import net.sqlcipher.database.SQLiteDatabase;
import org.junit.Ignore;

@Ignore
public class DatabaseSessionIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private DatabaseHelper dbHelper;

    public DatabaseSessionIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        dbHelper = new DatabaseHelper("SampleDBKey", getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        @Cleanup SQLiteDatabase session = dbHelper.openSession();
        session.execSQL("DROP TABLE CHILDREN");
        dbHelper.close();
    }

    public void testCreateTableAndAccessIt(){
        @Cleanup SQLiteDatabase session = dbHelper.openSession();
        session.execSQL("CREATE TABLE CHILDREN (CHILD_NAME VARCHAR(255),CHILD_ID VARCHAR(255))");

        ContentValues values = new ContentValues();
        values.put("CHILD_NAME", "child name");
        values.put("CHILD_ID", "1234");
        session.insert("CHILDREN", null, values);

        Cursor cursor = session.rawQuery("SELECT CHILD_NAME FROM CHILDREN WHERE CHILD_ID=?", new String[]{"1234"});
        while (cursor.moveToNext()) {
            assertEquals(cursor.getString(0), "child name");
        }
    }

}
