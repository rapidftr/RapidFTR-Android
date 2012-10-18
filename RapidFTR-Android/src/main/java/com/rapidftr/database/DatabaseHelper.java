package com.rapidftr.database;

import com.rapidftr.RapidFtrApplication;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    public DatabaseHelper() {
        super(RapidFtrApplication.getContext(), "rapidftr", null, 1);
        SQLiteDatabase.loadLibs(RapidFtrApplication.getContext());
    }

    public DatabaseSession getSession() {
        database = new DatabaseHelper().getWritableDatabase(RapidFtrApplication.getDbKey());
        return new DatabaseSession(database);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
