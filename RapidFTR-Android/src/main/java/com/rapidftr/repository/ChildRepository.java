package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
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
import static java.lang.String.format;

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

    public List<Child> getChildrenByOwner() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE child_owner = ? ORDER BY id", new String[]{userName});
        return toChildren(cursor);
    }

    public List<Child> getAllChildren() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children",new String[]{});
        return toChildren(cursor);
    }


    public void createOrUpdate(Child child) throws JSONException {
        Log.e("ChildRepository", child.toString());
        ContentValues values = new ContentValues();
        values.put(Database.ChildTableColumn.owner.getColumnName(), child.getOwner());
        values.put(id.getColumnName(), child.getId());
        values.put(Database.ChildTableColumn.content.getColumnName(), child.toString());
        values.put(Database.ChildTableColumn.synced.getColumnName(), child.isSynced());
        values.put(Database.ChildTableColumn.created_at.getColumnName(), child.getCreatedAt());
        addHistory(child);
        long id = session.replace(Database.child.getTableName(), null, values);
        if (id <= 0)
            throw new IllegalArgumentException();
    }

    private void addHistory(Child child) throws JSONException {
        Child existingChild = get(child.getId());
        if(existingChild == null)
            return;
        child.put("histories", convertToString(child.changeLogs(existingChild)));
    }

    private String convertToString(List<Child.History> histories) {
        StringBuffer json = new StringBuffer("[");
        for (Child.History history : histories) {
            json.append(history.toString());
        }
        return json.append("]").toString();
    }

    public void update(Child child) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(Database.ChildTableColumn.content.getColumnName(), child.toString());
        values.put(Database.ChildTableColumn.synced.getColumnName(), child.isSynced());

        session.update(Database.child.getTableName(), values, format("%s=?", id.getColumnName()), new String[]{child.getId()});
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
