package com.rapidftr.javascript;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

public class DatabaseInterface {

    Context mContext;
    public static final String PASSWORD = "demo";
    private static final String DATABASE_NAME = "rapidftr.db";
    private static final String TABLE_NAME = "children";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CHILD_DATA = "child_data";
    private SQLiteDatabase database;


    public DatabaseInterface(Context c) {
        mContext = c;
        initializeDB();
    }

    private void initializeDB() {
        SQLiteDatabase.loadLibs(mContext);
        File databaseFile = mContext.getDatabasePath(DATABASE_NAME);
        databaseFile.mkdirs();
        databaseFile.delete();
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
        database.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " integer primary key autoincrement ,"
                + COLUMN_CHILD_DATA + " text not null )");

        database.close();
    }

    private void closeDB() {
        database.close();
    }

    private void openDB() {
        File databaseFile = mContext.getDatabasePath(DATABASE_NAME);
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
    }

    public void displayJSONObject(String jsonObject) {
        System.out.println(jsonObject);
    }

    public void storeRecord(String childRecord) {
        displayJSONObject(childRecord);
        openDB();
        database.execSQL("insert into " + TABLE_NAME + "("+COLUMN_CHILD_DATA+") values(?)", new Object[]{childRecord});
        closeDB();
    }


    private void retrieveRecords() {
        openDB();
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        Integer numberOfRecords =cursor.getCount();
        Toast.makeText(mContext,numberOfRecords.toString(),Toast.LENGTH_LONG).show();
        cursor.close();
        closeDB();
    }


}