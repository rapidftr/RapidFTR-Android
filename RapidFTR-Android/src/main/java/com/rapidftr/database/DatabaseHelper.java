package com.rapidftr.database;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.Closeable;

public class DatabaseHelper extends SQLiteOpenHelper implements Closeable {

    public static final String DB_NAME = "rapidftr.db";
    public static final int DB_VERSION = 1;

    public static final String DB_CHILD_TABLE = "children";
    public static final String DB_CHILD_ID = "id";
    public static final String DB_CHILD_CONTENT = "child_json";
    public static final String DB_CHILD_OWNER = "child_owner";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + DB_CHILD_TABLE + "("
            + DB_CHILD_ID + " text primary key, "
            + DB_CHILD_CONTENT + " text not null,"
            + DB_CHILD_OWNER + " text not null"
        + ");";

    private final String dbKey;

    @Inject
    public DatabaseHelper(@Named("DB_KEY") String dbKey, Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        SQLiteDatabase.loadLibs(context);

        this.dbKey = dbKey;
    }

    public SQLiteDatabase openSession() {
        return getWritableDatabase(dbKey);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
       database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}
