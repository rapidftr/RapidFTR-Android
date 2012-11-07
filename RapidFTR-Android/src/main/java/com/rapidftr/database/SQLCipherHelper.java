package com.rapidftr.database;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.Getter;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import static com.rapidftr.database.Database.ChildTableColumn;

public class SQLCipherHelper extends SQLiteOpenHelper implements DatabaseHelper {

    public static final int DB_VERSION = 1;

    public static final String DATABASE_CREATE = "create table "
        + Database.child.getTableName() + "("
            + ChildTableColumn.id.getColumnName() + " text primary key, "
            + ChildTableColumn.content.getColumnName() + " text not null,"
            + ChildTableColumn.owner.getColumnName() + " text not null,"
            + ChildTableColumn.synced.getColumnName() + " text not null"
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
