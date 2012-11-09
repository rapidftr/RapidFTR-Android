package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import com.rapidftr.activity.BaseActivityIntegrationTest;
import com.rapidftr.activity.LoginActivity;
import lombok.Cleanup;
import org.junit.Test;

import static com.rapidftr.database.Database.ChildTableColumn;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


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

    @Test
    public void shouldGetCountOFAllChildren() {
        @Cleanup Cursor cursor = session.rawQuery("select count(1) from children", new String[]{});
        cursor.moveToNext();
        assertEquals(cursor.getInt(0), 0);
    }

    @Test
    public void ShouldBeAbleToDeleteAChildRecord() {
        ContentValues values = new ContentValues();
        values.put(ChildTableColumn.id.getColumnName(), "id1");
        values.put(ChildTableColumn.owner.getColumnName(), "owner1");
        values.put(ChildTableColumn.content.getColumnName(), "content1");
        values.put(ChildTableColumn.synced.getColumnName(), "false");

        long id = session.insert("children", null, values);
        assertThat(id, is(notNullValue()));

        int deleted = session.delete("children", "id = ?", new String[] { "id1" });
        assertThat(deleted, is(1));
    }

    @Test(expected = Exception.class)
    public void shouldNotBeAbleToAccessDatabaseWithIncorrectDecrypitionKey() {
        helper = new SQLCipherHelper("test_db", "wrong_password", getActivity());
    }

}
