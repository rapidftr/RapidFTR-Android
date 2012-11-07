package com.rapidftr.database;

import java.io.Closeable;

public interface DatabaseHelper extends Closeable {

    public static final String DB_CHILD_TABLE = "children";
    public static final String DB_CHILD_ID = "id";
    public static final String DB_CHILD_CONTENT = "child_json";
    public static final String DB_CHILD_OWNER = "child_owner";
    public static final String DB_CHILD_SYNCED = "synced";

    public DatabaseSession getSession();

}
