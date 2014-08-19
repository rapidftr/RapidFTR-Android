package com.rapidftr.model;

import android.database.Cursor;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rapidftr.database.Database;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.database.Database.EnquiryTableColumn.content;
import static com.rapidftr.database.Database.EnquiryTableColumn.potential_matches;

public class Enquiry extends BaseModel {

    private ArrayList<String> NONE_CRITERIA_FIELDS = new ArrayList<String>();
    public static final String ENQUIRY_FORM_NAME = "Enquiries";

    public Enquiry() throws JSONException {
        super();
        this.setUniqueId(createUniqueId());
    }

    public Enquiry(String content, String createdBy) throws JSONException {
        super(content);
        this.setCreatedBy(createdBy);
        this.setUniqueId(createUniqueId());
        this.setLastUpdatedAt(RapidFtrDateTime.now().defaultFormat());
    }

    public Enquiry(Cursor cursor) throws JSONException {
        super(cursor.getString(cursor.getColumnIndex(content.getColumnName())));

        for (Database.EnquiryTableColumn column : Database.EnquiryTableColumn.values()) {
            final int columnIndex = cursor.getColumnIndex(column.getColumnName());
            if (columnIndex < 0) {
                throw new IllegalArgumentException("Column " + column.getColumnName() + " does not exist");
            }

            if (column.getPrimitiveType().equals(Boolean.class)) {
                this.put(column.getColumnName(), cursor.getInt(columnIndex) == 1);
            } else if (column.equals(content)) {
                continue;
            } else {
                this.put(column.getColumnName(), cursor.getString(columnIndex));
            }
        }
    }

    public Enquiry(String enquiryJSON) throws JSONException {
        super(enquiryJSON);
    }

    public List<Child> getPotentialMatches(ChildRepository childRepository) throws JSONException {
        try {

            JSONArray matchingChildId = new JSONArray(getPotentialMatchingIds());

            List<String> matchingChildList = getListOfMatchingChildrenFrom(matchingChildId);

            return childRepository.getAllWithInternalIds(new ArrayList<String>(matchingChildList));
        } catch (JSONException exception) {
            return new ArrayList<Child>();
        }
    }


    private List<String> getListOfMatchingChildrenFrom(JSONArray matchingChildId) throws JSONException {
        List<String> matchingChildList = new ArrayList<String>();

        for (int i = 0; i < matchingChildId.length(); i++) {
            matchingChildList.add((String) matchingChildId.get(i));
        }
        return matchingChildList;
    }

    private void setColumn(Database.EnquiryTableColumn column, String value) throws JSONException {
        put(column.getColumnName(), value);
    }

    private ArrayList<String> getKeys() {
        Iterator keys = this.keys();
        ArrayList<String> enquiryKeys = new ArrayList<String>();
        while (keys.hasNext()) {
            enquiryKeys.add(keys.next().toString());
        }
        return enquiryKeys;
    }

    public boolean isValid() {
        int numberOfInternalFields = names().length();

        for (Database.EnquiryTableColumn field : Database.EnquiryTableColumn.internalFields()) {
            if (has(field.getColumnName())) {
                numberOfInternalFields--;
            }
        }
        return numberOfInternalFields > 0;
    }

    public JSONObject values() throws JSONException {
        Iterable<Object> systemFields = Iterables.transform(Database.EnquiryTableColumn.fields(), new Function<Database.EnquiryTableColumn, Object>() {
            @Override
            public Object apply(Database.EnquiryTableColumn enquiryTableColumn) {
                return enquiryTableColumn.getColumnName();
            }
        });
        List<Object> fields = Lists.newArrayList(systemFields);
        return new JSONObject(this, fields.toArray(new String[fields.size()]));
    }

    public String getPotentialMatchingIds() {
        String ids = getString(potential_matches.getColumnName());
        if (ids == null)
            return "";
        else
            return ids;
    }

    public String getInternalId() throws JSONException {
        return getString(internal_id.getColumnName());
    }
}
