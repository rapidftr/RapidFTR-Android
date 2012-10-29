package com.rapidftr.database;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.Getter;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SQLCipherHelper extends SQLiteOpenHelper implements DatabaseHelper {

    public static final int DB_VERSION = 1;

    public static final String DB_CHILD_TABLE = "children";
    public static final String DB_CHILD_ID = "id";
    public static final String DB_CHILD_CONTENT = "child_json";
    public static final String DB_CHILD_OWNER = "child_owner";

    public static final String DATABASE_CREATE = "create table "
        + DB_CHILD_TABLE + "("
            + DB_CHILD_ID + " text primary key, "
            + DB_CHILD_CONTENT + " text not null,"
            + DB_CHILD_OWNER + " text not null"
        + ");";

    protected @Getter final DatabaseSession session;

    @Inject
    public SQLCipherHelper(@Named("DB_NAME") String dbName, @Named("DB_KEY") String dbKey, Context context) {
        super(context, dbName, null, DB_VERSION);
        SQLiteDatabase.loadLibs(context);

        this.session = new SQLCipherSession(getWritableDatabase(dbKey));
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
       database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
    }

    @Override
    public void close() {
        super.close();
        SQLiteDatabase.releaseMemory();
    }

}
