package com.rapidftr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE CHILDREN (" +
            "OWNER_USERNAME VARCHAR(255)," +
            "CHILD_ID VARCHAR(255)," +
            "CONTENT VARCHAR(9999))";

    public DatabaseHelper(Context context) {
        super(context, "rapidftr", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //not needed
    }
}
