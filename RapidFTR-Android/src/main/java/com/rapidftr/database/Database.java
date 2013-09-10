package com.rapidftr.database;

import com.google.common.base.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Iterables.filter;

public enum Database {

    child("children"), enquiry("enquiry"),
    ;
    private String tableName;

    Database(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public enum BooleanColumn {
        trueValue("1", true),
        falseValue("0", false);
        private String columnValue;
        private boolean booleanValue;

        BooleanColumn(String columnValue, boolean booleanValue) {
            this.columnValue = columnValue;
            this.booleanValue = booleanValue;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public static BooleanColumn from(String booleanValue) {
            for (BooleanColumn booleanColumn : values()) {
                if(booleanColumn.getColumnValue().equals(booleanValue)){
                    return booleanColumn;
                }
            }
            return null;
        }

        public boolean toBoolean() {
            return booleanValue;
        }
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    public enum ChildTableColumn {
        id("id"),
        name("name"),
        content("child_json"),
        owner("child_owner"),
        synced("synced"),
        syncLog("syncLog"),

        internal_id("_id", true, false),
        internal_rev("_rev", true, false),
        unique_identifier("unique_identifier", true, false),
        created_by("created_by", true, false),
        last_updated_at("last_updated_at", true, false),

        revision("_rev", true, true),
        thumbnail("_thumbnail", true, true),
        created_at("created_at", true, true),
        created_organisation("created_organisation", true, false),

        last_synced_at("last_synced_at",true, true);
        private @Getter final String columnName;
        private final boolean isInternal;
        private final boolean isSystem;

        ChildTableColumn(String columnName) {
            this(columnName, false, false);
        }

        public static Iterable<ChildTableColumn> internalFields() {
            List<ChildTableColumn> allColumns = Arrays.asList(ChildTableColumn.values());
            return filter(allColumns, new Predicate<ChildTableColumn>() {
                public boolean apply(ChildTableColumn column) {
                    return column.isInternal;
                }
            });
        }

        public static Iterable<ChildTableColumn> systemFields() {
            List<ChildTableColumn> allColumns = Arrays.asList(ChildTableColumn.values());
            return filter(allColumns, new Predicate<ChildTableColumn>() {
                public boolean apply(ChildTableColumn column) {
                    return column.isSystem;
                }
            });
        }
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    public enum EnquiryTableColumn {
        id("id"),
        name("reporter_name"),
        content("criteria"),
        owner("enquiry_owner"),
        synced("synced"),

        internal_id("_id", true, false),
        created_by("created_by", true, false),

        last_updated_at("last_updated_at", true, false),

        created_at("created_at", true, true),

        last_synced_at("last_synced_at",true, true);
        private @Getter final String columnName;
        private final boolean isInternal;
        private final boolean isSystem;

        EnquiryTableColumn(String columnName) {
            this(columnName, false, false);
        }

        public static Iterable<EnquiryTableColumn> internalFields() {
            List<EnquiryTableColumn> allColumns = Arrays.asList(EnquiryTableColumn.values());
            return filter(allColumns, new Predicate<EnquiryTableColumn>() {
                public boolean apply(EnquiryTableColumn column) {
                    return column.isInternal;
                }
            });
        }

        public static Iterable<EnquiryTableColumn> systemFields() {
            List<EnquiryTableColumn> allColumns = Arrays.asList(EnquiryTableColumn.values());
            return filter(allColumns, new Predicate<EnquiryTableColumn>() {
                public boolean apply(EnquiryTableColumn column) {
                    return column.isSystem;
                }
            });
        }
    }

}