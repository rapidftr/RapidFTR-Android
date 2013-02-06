package com.rapidftr.database.migration;

import com.google.common.base.Predicate;
import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;

import java.util.List;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public enum Migrations {


    v001_createChildTable(1, MigrationSQL.createChildTable),
    v001_addCreatedAtColumn(1, MigrationSQL.addCreatedAtColumn),
    v001_addUpdatedAtColumn(1, MigrationSQL.addLastUpdatedAtColumn),
    v001_addNameColumn(1, MigrationSQL.addNameColumn),
    v001_add_idColumn(1, MigrationSQL.addIdColumn),
    v001_add_revColumn(1, MigrationSQL.addRevColumn),
    v001_add_last_synced_at_column(1,MigrationSQL.addLastSyncedAtColumn)
    ;

    private int databaseVersion;
    private String sql;


    Migrations(int databaseVersion, String sql) {
        this.databaseVersion = databaseVersion;
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public boolean isForVersion(int databaseVersion){
        return this.databaseVersion == databaseVersion;
    }

    public static List<Migrations> forVersion(final int databaseVersion){
        return newArrayList(filter(asList(values()), new Predicate<Migrations>() {
            public boolean apply(Migrations migration) {
                return migration.isForVersion(databaseVersion);
            }
        }));
    }

}

class MigrationSQL {

    public static final String createChildTable = "create table "
            + Database.child.getTableName() + "("
            + Database.ChildTableColumn.id.getColumnName() + " text primary key,"
            + Database.ChildTableColumn.owner.getColumnName() + " text not null,"
            + Database.ChildTableColumn.content.getColumnName() + " text not null,"
            + Database.ChildTableColumn.synced.getColumnName() + " text not null"
            + ");";

    public static final String addCreatedAtColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.created_at.getColumnName()
            + " text not null default '"+ RapidFtrDateTime.now().defaultFormat() +"'";

    public static final String addLastUpdatedAtColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.last_updated_at.getColumnName()
            + " text";

    public static final String addNameColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.name.getColumnName()
            + " text";

    public static String addIdColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.internal_id.getColumnName()
            + " text ";

    public static String addRevColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.internal_rev.getColumnName()
            + " text ";

    public static String addLastSyncedAtColumn = " ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.last_synced_at.getColumnName()
            + " text not null default '"+ RapidFtrDateTime.now().defaultFormat() +"'";
}

