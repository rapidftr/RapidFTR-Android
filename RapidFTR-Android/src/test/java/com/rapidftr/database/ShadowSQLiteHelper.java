package com.rapidftr.database;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.rapidftr.utils.RapidFtrDateTime;
import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * Stub SQLite Helper class which uses an in-memory database provided by Robolectric
 * Database is freshly created Whenever "getWritableDatabase" or "getReadableDatabase" is called
 */
public class ShadowSQLiteHelper extends SQLiteOpenHelper implements DatabaseHelper {

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance() {
        return instance == null ? resetDatabase() : instance;
    }

    public static DatabaseHelper resetDatabase() {
        return (instance = new ShadowSQLiteHelper());
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    public static class ShadowSQLiteSession implements DatabaseSession {

        @Delegate(types = DatabaseSession.class)
        private final SQLiteDatabase database;

    }

    private @Getter DatabaseSession session;

    public ShadowSQLiteHelper() {
        super(new Activity(), "test_database", null, 2);
        session = new ShadowSQLiteSession(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLCipherHelper.CREATE_CHILD_TABLE);
        String now = RapidFtrDateTime.now().defaultFormat();
        String addCreatedAtColumn = "ALTER TABLE "
                + Database.child.getTableName()
                + " ADD COLUMN "
                + Database.ChildTableColumn.created_at.getColumnName()
                + " text not null default '"+ now +"'";
        String addUpdatedAtColumn = "ALTER TABLE "
                + Database.child.getTableName()
                + " ADD COLUMN "
                + Database.ChildTableColumn.updated_at.getColumnName()
                + " text";
        db.execSQL(addCreatedAtColumn);
        db.execSQL(addUpdatedAtColumn);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
