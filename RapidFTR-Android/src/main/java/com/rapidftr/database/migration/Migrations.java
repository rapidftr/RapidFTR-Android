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
    v002_addCreatedAtColumn(2, MigrationSQL.addCreatedAtColumn),
    v002_addUpdatedAtColumn(2, MigrationSQL.addUpdatedAtColumn),
    v003_addNameColumn(3, MigrationSQL.addNameColumn),
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

    public static final String addUpdatedAtColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.updated_at.getColumnName()
            + " text";

    public static final String addNameColumn = "ALTER TABLE "
            + Database.child.getTableName()
            + " ADD COLUMN "
            + Database.ChildTableColumn.name.getColumnName()
            + " text";

}

