package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class  ChildRecordStorage {

    private String username;
    private String password;

    public ChildRecordStorage(String username, String password) {
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

        openDatabase().insert("CHILDREN", values);
        closeDatabase();
    }

    private DatabaseSession openDatabase() {
        return new DatabaseHelper().getSession();
    }

    private void closeDatabase() {
        new DatabaseHelper().close();
    }

}
