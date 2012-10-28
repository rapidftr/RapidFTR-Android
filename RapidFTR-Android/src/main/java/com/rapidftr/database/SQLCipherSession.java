package com.rapidftr.database;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import net.sqlcipher.database.SQLiteDatabase;

@RequiredArgsConstructor
public class SQLCipherSession implements DatabaseSession {

    @Delegate(types = DatabaseSession.class)
    private final SQLiteDatabase database;

}
