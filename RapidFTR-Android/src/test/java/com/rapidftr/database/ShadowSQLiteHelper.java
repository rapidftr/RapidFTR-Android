package com.rapidftr.database;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;

public class ShadowSQLiteHelper extends SQLiteOpenHelper implements DatabaseHelper {

    @RequiredArgsConstructor
    public static class ShadowSQLiteSession implements DatabaseSession {

        @Delegate(types = DatabaseSession.class)
        private final SQLiteDatabase database;

    }

    public ShadowSQLiteHelper() {
        super(new Activity(), "mydb", null, 1);
    }

    @Override
    public DatabaseSession openSession() {
        return new ShadowSQLiteSession(getWritableDatabase());
    }

    @Override
    public void close() {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLCipherHelper.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
