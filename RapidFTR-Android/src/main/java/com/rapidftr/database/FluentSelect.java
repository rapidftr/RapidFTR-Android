package com.rapidftr.database;

import android.database.Cursor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class FluentSelect {

    public interface Interface {
        public FluentSelect select();
    }

    protected String table;
    protected List<String> columns = new ArrayList<String>();
    protected StringBuffer selection;
    protected List<String> selectionArgs = new ArrayList<String>();
    protected StringBuffer orderBy;
    protected String limit;

    protected final DatabaseSession session;
    protected @Getter Cursor cursor;

    public FluentSelect from(String table) {
        this.table = table;
        return this;
    }

    public FluentSelect select(String column) {
        this.columns.add(column);
        return this;
    }

    public FluentSelect where(String criteria, String arg) {
        this.selection.append("(").append(criteria).append(")");
        this.selectionArgs.add(arg);
        return this;
    }

    public FluentSelect and(String criteria, String arg) {
        this.selection.append(" AND ");
        return this.where(criteria, arg);
    }

    public FluentSelect or(String criteria, String arg) {
        this.selection.append(" OR ");
        return this.where(criteria, arg);
    }

    public FluentSelect orderBy(String orderBy, boolean asc) {
        this.orderBy.append(orderBy).append(asc ? " ASC" : " DESC");
        return this;
    }

    public FluentSelect limit(String limit) {
        this.limit = limit;
        return this;
    }

    public FluentSelect execute() {
        this.cursor = session.query(table, columns.toArray(new String[columns.size()]), selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]), null, null, orderBy.toString(), limit);
        return this;
    }

}
