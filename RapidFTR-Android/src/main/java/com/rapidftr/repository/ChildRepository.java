package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Child;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.database.Database.BooleanColumn;
import static com.rapidftr.database.Database.BooleanColumn.falseValue;
import static com.rapidftr.database.Database.ChildTableColumn.id;

public class ChildRepository implements Closeable {

    protected final String userName;
    protected final DatabaseSession session;

    @Inject
    public ChildRepository(@Named("USER_NAME") String userName, DatabaseSession session) {
        this.userName = userName;
        this.session = session;
    }

    public Child get(String id) throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE id = ? AND child_owner = ?", new String[]{id, userName});
        return cursor.moveToNext() ? childFrom(cursor) : null;
    }

    public boolean exists(String childId) {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json FROM children WHERE id = ?", new String[]{childId});
        return cursor.getCount() > 0;
    }

    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM children WHERE child_owner = ?", new String[]{userName});
        return cursor.moveToNext() ? cursor.getInt(0) : 0;
    }

    public List<Child> all() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE child_owner = ? ORDER BY id", new String[]{userName});
        return toChildren(cursor);
    }

    public void create(Child child) throws JSONException {
        if (!userName.equals(child.getOwner()))
            throw new IllegalArgumentException();

        ContentValues values = new ContentValues();
        values.put(Database.ChildTableColumn.owner.getColumnName(), child.getOwner());
        values.put(id.getColumnName(), child.getId());
        values.put(Database.ChildTableColumn.content.getColumnName(), child.toString());
        values.put(Database.ChildTableColumn.synced.getColumnName(), child.isSynced());
        long id = session.replace(Database.child.getTableName(), null, values);
        if (id <= 0)
            throw new IllegalArgumentException();
    }

    public void update(Child child) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(Database.ChildTableColumn.content.getColumnName(), child.toString());
        values.put(Database.ChildTableColumn.synced.getColumnName(), child.isSynced());

        session.update(Database.child.getTableName(), values, String.format("%s=?", id.getColumnName()), new String[]{child.getId()});
    }

    public List<Child> toBeSynced() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE synced = ?", new String[]{falseValue.getColumnValue()});
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
            children.add(childFrom(cursor));
        }
        return children;
    }

    private Child childFrom(Cursor cursor) throws JSONException {
        return new Child(cursor.getString(0), BooleanColumn.from(cursor.getString(1)).toBoolean());
    }

}
