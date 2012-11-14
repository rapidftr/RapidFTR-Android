package com.rapidftr.database;

import java.io.Closeable;

public interface DatabaseHelper extends Closeable {

    public DatabaseSession getSession();
}
