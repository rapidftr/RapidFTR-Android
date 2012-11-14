package com.rapidftr.database;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.utils.RapidFtrDateTime;
import lombok.Getter;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import static com.rapidftr.database.Database.ChildTableColumn;

public class SQLCipherHelper extends SQLiteOpenHelper implements DatabaseHelper {

    public static final int DB_VERSION = 2;

    public static final String CREATE_CHILD_TABLE = "create table "
        + Database.child.getTableName() + "("
            + ChildTableColumn.id.getColumnName() + " text primary key,"
            + ChildTableColumn.owner.getColumnName() + " text not null,"
            + ChildTableColumn.content.getColumnName() + " text not null,"
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
       database.execSQL(CREATE_CHILD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String now = RapidFtrDateTime.now().defaultFormat();
        String addCreatedAtColumn = "ALTER TABLE "
                + Database.child.getTableName()
                + " ADD COLUMN "
                + ChildTableColumn.created_at.getColumnName()
                + " text not null default '"+ now +"'";
        String addUpdatedAtColumn = "ALTER TABLE "
                + Database.child.getTableName()
                + " ADD COLUMN "
                + ChildTableColumn.updated_at.getColumnName()
                + " text";
        database.execSQL(addCreatedAtColumn);
        database.execSQL(addUpdatedAtColumn);
    }

    @Override
    public void close() {
        super.close();
        SQLiteDatabase.releaseMemory();
    }

}
