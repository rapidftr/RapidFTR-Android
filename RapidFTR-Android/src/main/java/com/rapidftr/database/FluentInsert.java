package com.rapidftr.database;

import android.content.ContentValues;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class FluentInsert {

    public interface Interface {
        public FluentInsert insert();
    }

    protected ContentValues values;
    protected final DatabaseSession session;

    public FluentInsert set(String column, String value) {
        values.put(column, value);
        return this;
    }

    public long insertInto(String table) {
        return session.insert(table, null, values);
    }

}
