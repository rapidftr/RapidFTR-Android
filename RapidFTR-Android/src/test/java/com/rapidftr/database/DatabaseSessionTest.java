package com.rapidftr.database;

import android.content.ContentValues;
import com.rapidftr.CustomTestRunner;
import net.sqlcipher.database.SQLiteDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class DatabaseSessionTest{

    @Mock private SQLiteDatabase database;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldCallDatabaseExecuteAndCloseSession(){
        DatabaseSession session = new DatabaseSession(database);
        session.execute("some sql");

        verify(database).execSQL("some sql");
        verify(database).close();
    }

    @Test
    public void shouldCallDatabaseRawSQLAndCloseSession(){
        DatabaseSession session = new DatabaseSession(database);
        String rawSQL = "raw sql";
        String[] params = new String[]{"1", "2"};
        session.rawQuery(rawSQL, params);

        verify(database).rawQuery(rawSQL, params);
        verify(database).close();
    }

    @Test
    public void shouldCallDatabaseDeleteAndCloseSession(){
        DatabaseSession session = new DatabaseSession(database);
        String where = "condition";
        String tableName = "table-name";
        String[] args = new String[]{"1", "2"};
        session.delete(tableName, where, args);

        verify(database).delete(tableName, where, args);
        verify(database).close();
    }

    @Test
    public void shouldCallInsertDatabaseAndCloseSession(){
        DatabaseSession session = new DatabaseSession(database);

        String tableName = "table-name";
        ContentValues contentValues = new ContentValues();
        session.insert(tableName, contentValues);

        verify(database).insert(tableName, null, contentValues);
        verify(database).close();
    }
}
