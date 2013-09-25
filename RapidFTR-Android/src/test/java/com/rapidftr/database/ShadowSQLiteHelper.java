package com.rapidftr.database;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.rapidftr.database.migration.Migrations;
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
        return (instance = new ShadowSQLiteHelper("test_database"));
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    public static class ShadowSQLiteSession implements DatabaseSession {

        @Delegate(types = DatabaseSession.class)
        private final SQLiteDatabase database;

    }

    private @Getter DatabaseSession session;

    public ShadowSQLiteHelper(String dbName) {
        super(new Activity(), dbName, null, 2);
        session = new ShadowSQLiteSession(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (Migrations migration : Migrations.values()) {
            database.execSQL(migration.getSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Migrations migration : Migrations.forVersion(newVersion)) {
            db.execSQL(migration.getSql());
        }
    }

}
