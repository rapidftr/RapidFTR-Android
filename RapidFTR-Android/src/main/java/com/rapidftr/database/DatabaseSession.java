package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class DatabaseSession{
    private SQLiteDatabase database;

    public DatabaseSession(SQLiteDatabase database) {
        this.database = database;
    }

    public void insert(String tableName, ContentValues contentValues) {
        database.insert(tableName, null, contentValues);
        database.close();
    }

    public void delete(String tableName, String whereClause, String[] args) {
        database.delete(tableName, whereClause, args);
        database.close();
    }

    public Cursor rawQuery(String sql, String[] args) {
        Cursor cursor = database.rawQuery(sql, args);
        database.close();
        return cursor;
    }

    public void execute(String sql) {
        database.execSQL(sql);
        database.close();
    }
}
