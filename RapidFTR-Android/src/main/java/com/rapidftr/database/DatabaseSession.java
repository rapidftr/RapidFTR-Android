package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Closeable;

public interface DatabaseSession extends Closeable {

    public int delete(String table, String whereClause, String[] whereArgs);
    public Cursor rawQuery(String sql, String[] selectionArgs);
    public void execSQL(String sql);
    public long insert(String table, String nullColumnHack, ContentValues values);

}
