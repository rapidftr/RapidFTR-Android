package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Child;
import com.rapidftr.utils.RapidFtrDateTime;
import lombok.Cleanup;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.database.Database.BooleanColumn;
import static com.rapidftr.database.Database.BooleanColumn.falseValue;
import static com.rapidftr.database.Database.ChildTableColumn.id;
import static com.rapidftr.model.Child.History.HISTORIES;
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
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE id = ?", new String[]{id});
        if (cursor.moveToNext()) {
            return childFrom(cursor);
        } else {
            throw new NullPointerException(id);
        }
    }

    public boolean exists(String childId) {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json FROM children WHERE id = ?", new String[]{childId == null ? "" : childId});
        return cursor.moveToNext() && cursor.getCount() > 0;
    }

    public int size() {
        @Cleanup Cursor cursor = session.rawQuery("SELECT COUNT(1) FROM children WHERE child_owner = ?", new String[]{userName});
        return cursor.moveToNext() ? cursor.getInt(0) : 0;
    }

    public List<Child> getChildrenByOwner() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE child_owner = ? ORDER BY name", new String[]{userName});
        return toChildren(cursor);
    }

    public List<Child> getMatchingChildren(String subString) throws JSONException {
        String searchString = String.format("%%%s%%", subString);
        @Cleanup Cursor cursor = session.rawQuery("SELECT child_json, synced FROM children WHERE name LIKE ? or id LIKE ?", new String[]{searchString,searchString});
        return toChildren(cursor);
    }


    public void createOrUpdate(Child child) throws JSONException {
        ContentValues values = new ContentValues();
        if (exists(child.getUniqueId())) {
            addHistory(child);
        }
        child.setLastUpdatedAt(getTimeStamp());
        values.put(Database.ChildTableColumn.owner.getColumnName(), child.getOwner());
        values.put(id.getColumnName(), child.getUniqueId());
        values.put(Database.ChildTableColumn.name.getColumnName(), child.getName());
        values.put(Database.ChildTableColumn.content.getColumnName(), child.toString());
        values.put(Database.ChildTableColumn.synced.getColumnName(), child.isSynced());
        values.put(Database.ChildTableColumn.created_at.getColumnName(), child.getCreatedAt());
        long id = session.replace(Database.child.getTableName(), null, values);
        if (id <= 0)
            throw new IllegalArgumentException();
    }

    private void addHistory(Child child) throws JSONException {
        Child existingChild = get(child.getUniqueId());
        JSONArray existingHistories = (JSONArray) existingChild.opt(HISTORIES);
        List<Child.History> histories = child.changeLogs(existingChild);
        if(histories.size() > 0 || (existingHistories != null && existingHistories.length() > 1))
            child.put(HISTORIES, convertToString(existingHistories, histories));
    }

    private String convertToString(JSONArray existingHistories, List<Child.History> histories) throws JSONException {
        StringBuffer json = new StringBuffer("[");
        for (int i = 0; existingHistories != null && (i < existingHistories.length()); i++) {
            json.append(existingHistories.get(i) + ",");
        }
        for (Child.History history : histories) {
            json.append(history.toString() + ",");
        }
        json.setLength(json.length() - 1);
        return json.append("]").toString();
    }

    public void update(Child child) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(Database.ChildTableColumn.content.getColumnName(), child.toString());
        values.put(Database.ChildTableColumn.synced.getColumnName(), child.isSynced());

        session.update(Database.child.getTableName(), values, format("%s=?", id.getColumnName()), new String[]{child.getUniqueId()});
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

    protected String getTimeStamp(){
        return RapidFtrDateTime.now().defaultFormat();
    }
}
