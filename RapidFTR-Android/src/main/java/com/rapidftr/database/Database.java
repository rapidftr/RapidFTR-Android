package com.rapidftr.database;

import com.google.common.base.Predicate;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Iterables.filter;

public enum Database {

    child("children"),
    ;
    private String tableName;

    Database(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public enum ChildTableColumn {
        id("id"),
        content("child_json"),
        owner("child_owner"),
        synced("synced", true),
        internal_id("_id", true),
        created_by("created_by", true),
        thumbnail("_thumbnail", true),
        ;

        private String columnName;
        private boolean isInternal;

        ChildTableColumn(String columnName) {
            this(columnName, false);
        }

        ChildTableColumn(String columnName, boolean isInternal) {
            this.columnName = columnName;
            this.isInternal = isInternal;
        }

        public String getColumnName() {
            return columnName;
        }

        public static Iterable<ChildTableColumn> internalFields(){
            List<ChildTableColumn> allColumns = Arrays.asList(ChildTableColumn.values());
            return filter(allColumns, new Predicate<ChildTableColumn>() {
                public boolean apply(ChildTableColumn column) {
                    return column.isInternal;
                }
            });
        }
    }

}