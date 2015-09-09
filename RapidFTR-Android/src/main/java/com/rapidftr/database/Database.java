package com.rapidftr.database;

import com.google.common.base.Predicate;
import com.rapidftr.model.PotentialMatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Iterables.filter;

public enum Database {

    child("children"), enquiry("enquiry"), potential_match("potential_match");
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
                if (booleanColumn.getColumnValue().equals(booleanValue)) {
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
        synced("synced", true, true),
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

        last_synced_at("last_synced_at", true, true);
        private
        @Getter
        final String columnName;
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
    public enum PotentialMatchTableColumn {
        id("id"),
        enquiry_id("enquiry_id"),
        child_id("child_id"),
        created_at("created_at", true, false),
        revision("_rev", true, true),
        confirmed("confirmed");

        private
        @Getter
        final String columnName;
        private final boolean isInternal;
        private final boolean isSystem;

        PotentialMatchTableColumn(String columnName) {
            this(columnName, false, false);
        }

        public static Iterable<PotentialMatchTableColumn> internalFields() {
            List<PotentialMatchTableColumn> allColumns = Arrays.asList(PotentialMatchTableColumn.values());
            return filter(allColumns, new Predicate<PotentialMatchTableColumn>() {
                public boolean apply(PotentialMatchTableColumn column) {
                    return column.isInternal;
                }
            });
        }

        public static Iterable<PotentialMatchTableColumn> systemFields() {
            List<PotentialMatchTableColumn> allColumns = Arrays.asList(PotentialMatchTableColumn.values());
            return filter(allColumns, new Predicate<PotentialMatchTableColumn>() {
                public boolean apply(PotentialMatchTableColumn column) {
                    return column.isSystem;
                }
            });
        }
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    public enum EnquiryTableColumn {
        id("id"),
        unique_identifier("unique_identifier", true, false),
        content("enquiry_json"),
        created_by("created_by", true, false),
        created_at("created_at", true, false),
        last_updated_at("last_updated_at", true, false),
        synced("synced", Boolean.class, true, true),
        internal_id("_id", true, false),
        internal_rev("_rev", true, false),
        revision("_rev", true, true);

        @Getter
        private final String columnName;

        @Getter
        private final Class<?> primitiveType;
        private final boolean isInternal;
        private final boolean isSystem;


        EnquiryTableColumn(String columnName) {
            this(columnName, String.class, false, false);
        }

        EnquiryTableColumn(String columnName, boolean isInternal, boolean isSystem) {
            this(columnName, String.class, isInternal, isSystem);
        }

        public static Iterable<EnquiryTableColumn> fields() {
            List<EnquiryTableColumn> allColumns = Arrays.asList(EnquiryTableColumn.values());
            return allColumns;
        }

        public static Iterable<EnquiryTableColumn> internalFields() {
            List<EnquiryTableColumn> allColumns = Arrays.asList(EnquiryTableColumn.values());

            return filter(allColumns, new Predicate<EnquiryTableColumn>() {
                @Override
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