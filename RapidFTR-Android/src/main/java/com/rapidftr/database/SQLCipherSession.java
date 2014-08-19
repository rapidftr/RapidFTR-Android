package com.rapidftr.database;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import net.sqlcipher.database.SQLiteDatabase;


@RequiredArgsConstructor(suppressConstructorProperties = true)
public class SQLCipherSession implements DatabaseSession {

    @Delegate(types = DatabaseSession.class)
    protected final SQLiteDatabase database;

}
