package com.rapidftr.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Child;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.database.DatabaseHelper.*;

public class ChildRepoistory implements Closeable {

    protected final String userName;
    protected final DatabaseSession session;

    @Inject
    public ChildRepoistory(@Named("USER_NAME") String userName, DatabaseSession session) {
        this.userName = userName;
        this.session = session;
    }

    public Child get(String id) throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json FROM children WHERE id = ? AND child_owner = ?", new String[]{id, userName});
        return cursor.moveToNext() ? new Child(cursor.getString(0)) : null;
    }

    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM children WHERE child_owner = ?", new String[]{userName});
        return cursor.moveToNext() ? cursor.getInt(0) : 0;
    }

    public List<Child> all() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json FROM children WHERE child_owner = ? ORDER BY id", new String[]{userName});
        return toChildren(cursor);
    }

    public void create(Child child) throws JSONException {
        if (!userName.equals(child.getOwner()))
            throw new IllegalArgumentException();

        ContentValues values = new ContentValues();
        values.put(DB_CHILD_OWNER, child.getOwner());
        values.put(DB_CHILD_ID, child.getId());
        values.put(DB_CHILD_CONTENT, child.toString());
        values.put(DB_CHILD_SYNCED, child.isSynced());
        long id = session.insert("CHILDREN", null, values);
        if (id <= 0)
            throw new IllegalArgumentException();
    }

    public List<Child> toBeSynced() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json FROM children WHERE synced = ?", new String[]{"0"});
        return toChildren(cursor);
    }

    @Override
    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Child> toChildren(Cursor cursor) throws JSONException {
        List<Child> children = new ArrayList<Child>();
        while (cursor.moveToNext()) {
            children.add(new Child(cursor.getString(0)));
        }
        return children;
    }

}
