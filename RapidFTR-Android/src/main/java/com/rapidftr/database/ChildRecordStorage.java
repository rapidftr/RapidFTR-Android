package com.rapidftr.database;

import android.content.ContentValues;
import android.database.Cursor;
import com.rapidftr.model.Child;
import lombok.Cleanup;
import net.sqlcipher.database.SQLiteDatabase;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.database.DatabaseHelper.*;

public class ChildRecordStorage implements Closeable {

    private final String username;
    private final String password;
    private final DatabaseHelper helper;
    private final SQLiteDatabase session;

    public ChildRecordStorage(String username, String password) {
        this.username = username;
        this.password = password;
        this.helper   = new DatabaseHelper();
        this.session  = helper.getSession();
    }

    public void clearAll() {
        session.delete("CHILDREN", " = ?", new String[]{username});
    }


    public List<Child> getAllChildren() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM CHILDREN WHERE OWNER_USERNAME=? ORDER BY CHILD_ID", new String[]{username});

        List<Child> children = new ArrayList<Child>();
        while (cursor.moveToNext()) {
            children.add(new Child(EncryptUtil.decrypt(cursor.getString(0), password)));
        }

        return children;
    }

    public void addChild(Child child) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(DB_CHILD_OWNER, child.getOwner());
        values.put(DB_CHILD_ID, child.getId());
        values.put(DB_CHILD_CONTENT, EncryptUtil.encrypt(child.toString(), password));

        session.insert("CHILDREN", null, values);
    }

    @Override
    public void close() throws IOException {
        session.close();
        helper.close();
    }

}
