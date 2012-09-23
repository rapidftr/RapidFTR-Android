package com.rapidftr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChildRecordStorage {

    private String username;
    private String password;
    private SQLiteDatabase database;
    private Context context;

    public ChildRecordStorage(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
    }

    public void clearAll() {
        openDatabase().delete("CHILDREN", "OWNER_USERNAME=?", new String[]{username});
        closeDatabase();
    }


    public List<String> getAllChildren() {
        Cursor cursor = openDatabase().rawQuery("SELECT CONTENT FROM CHILDREN WHERE OWNER_USERNAME=? ORDER BY CHILD_ID", new String[]{username});
        List<String> children = new ArrayList<String>();
        while (cursor.moveToNext()) {
            children.add(EncryptUtil.decrypt(cursor.getString(0), password));
        }
        closeDatabase();
        return children;
    }

    public void addChild(String id, String content) {
        ContentValues values = new ContentValues();
        values.put("OWNER_USERNAME", username);
        values.put("CHILD_ID", id);
        values.put("CONTENT", EncryptUtil.encrypt(content, password));

        openDatabase().insert("CHILDREN", null, values);
        closeDatabase();
    }

    private SQLiteDatabase openDatabase() {
        database = new DatabaseHelper(context).getWritableDatabase();
        return database;
    }

    private void closeDatabase() {
        database.close();
    }

}
