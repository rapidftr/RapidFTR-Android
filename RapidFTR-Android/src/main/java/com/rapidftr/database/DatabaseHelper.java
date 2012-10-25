package com.rapidftr.database;

import com.rapidftr.RapidFtrApplication;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;
    public static final String DATABASE_NAME = "rapidftr.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CHILDREN = "children";
    public static final String TABLE_CHILDREN_COLUMN_ID = "id";
    public static final String TABLE_CHILDREN_COLUMN_CHILD_JSON = "child_json";


    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CHILDREN + "(" + TABLE_CHILDREN_COLUMN_ID
            + " text primary key , " + TABLE_CHILDREN_COLUMN_CHILD_JSON
            + " text not null);";
    public DatabaseHelper() {
        super(RapidFtrApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase.loadLibs(RapidFtrApplication.getContext());
    }

    public DatabaseSession getSession() {
        database = new DatabaseHelper().getWritableDatabase(RapidFtrApplication.getDbKey());
        return new DatabaseSession(database);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
       database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
