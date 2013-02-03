package com.rapidftr.model;


import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.Database;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.model.Child.History.*;
import static com.rapidftr.utils.JSONMatcher.equalJSONIgnoreOrder;
import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class ChildTest {

    @Test
    public void shouldCreateChildWithBlankContent() throws JSONException {
        new Child("        ");
    }

    @Test
    public void shouldDecodeIDFromJSON() throws JSONException {
        Child child = new Child("{ 'unique_identifier' : 'test1' }");
        assertThat(child.getUniqueId(), is("test1"));
    }

    @Test
    public void shouldDecodeOwnerFromJSON() throws JSONException {
        Child child = new Child("{ 'created_by' : 'test1' }");
        assertThat(child.getOwner(), is("test1"));
    }

    @Test
    public void shouldDecodeString() throws JSONException {
        Child child = new Child("{ 'test1' :  'value1' }");
        assertThat(child.getString("test1"), is("value1"));
    }

    @Test
    public void shouldDecodeInteger() throws JSONException {
        Child child = new Child("{ 'test1' : 17 }");
        assertThat(child.getInt("test1"), is(17));
    }

    @Test
    public void shouldDecodeArrayOfStrings() throws JSONException {
        Child child = new Child("{ 'test1' : ['value1', 'value2', 'value3' ]}");
        assertThat(child.getJSONArray("test1").toString(), is(new JSONArray(Arrays.asList("value1", "value2", "value3")).toString()));
    }

    @Test
    public void shouldGenerateWithIdAndOwnerAndContent() throws JSONException {
        Child child = new Child("id1", "owner1", "{ 'test1' : 'value1' }");
        assertThat(child.getUniqueId(), is("id1"));
        assertThat(child.getOwner(), is("owner1"));
        assertThat(child.getString("test1"), is("value1"));
    }

    @Test
    public void shouldNotOverwriteCreatedAtIfGiven() throws JSONException {
        Child child = new Child(String.format("{ 'created_at' :  '%s' }", new RapidFtrDateTime(10, 2, 2012).defaultFormat()));
        assertThat(child.getCreatedAt(), is("2012-02-10 00:00:00"));
    }

    @Test
    public void shouldGenerateUniqueId() throws JSONException {
        Child child = new Child(null, "rapidftr", null);
        child = spy(child);

        doReturn("xyz").when(child).createUniqueId();

        child.generateUniqueId();
        assertThat(child.getUniqueId(), equalTo("xyz"));
    }

    @Test
    public void shouldNotOverwriteIdIfAlreadyPresent() throws JSONException {
        Child child = new Child("id1", "owner1", null);
        child.generateUniqueId();
        assertThat(child.getUniqueId(), equalTo("id1"));
    }

    @Test
    public void shouldReturnShortID() throws JSONException {
        Child child = new Child();
        child.setUniqueId("987654321");

        assertThat(child.getShortId(), equalTo("7654321"));
    }

    @Test
    public void testAtLeastOneFieldIsFilledExcludingIdAndOwner() throws JSONException {
        Child child = new Child("id1", "owner1", null);
        assertThat(child.isValid(), is(false));

        child.put("test1", "value1");
        assertThat(child.isValid(), is(true));

        child.remove("test1");
        assertThat(child.isValid(), is(false));
    }

    @Test
    public void shouldRemoveFromJSONArray() throws JSONException {
        Child child = new Child("{ 'test1' : ['value1', 'value2', 'value3' ]}");
        child.removeFromJSONArray("test1", "value1");
        assertThat(child.getJSONArray("test1").toString(), is(new JSONArray(Arrays.asList("value2", "value3")).toString()));
    }

    @Test
    public void shouldReturnNamesWithLengthOneInsteadOfNull() throws JSONException {
        Child child = new Child();
        assertThat(child.names().length(), equalTo(1));
    }

    @Test
    public void shouldRemoveFieldIfBlank() throws JSONException {
        Child child = new Child();
        child.put("name", "test");
        assertThat(child.values().names().length(), equalTo(1));

        child.put("name", "\r  \n  \r  \n");
        assertNull(child.values().names());
    }

    @Test
    public void shouldHaveFalseSyncStatusIfTheChildObjectIsCreated() throws JSONException {
        Child child = new Child();
        assertFalse(child.isSynced());
    }
    @Test
    public void shouldRemoveFieldIfNull() throws JSONException {
        Child child = new Child();
        child.put("name", "test");

        assertEquals(child.get("name"), "test");

        Object value = null;
        child.put("name", value);
        assertNull(child.opt("name"));
    }

    @Test
    public void shouldRemoveFieldIfJSONArrayIsEmtpy() throws JSONException {
        Child child = new Child();
        child.put("name", new JSONArray(Arrays.asList("one")));
        assertThat(child.values().names().length(), equalTo(1));

        child.put("name", new JSONArray());
        assertNull(child.values().names());
    }

    @Test
    public void shouldTrimFieldValues() throws JSONException {
        Child child = new Child();
        child.put("name", "\r\n line1 \r\n line2 \r\n \r\n");
        assertThat(child.getString("name"), equalTo("line1 \r\n line2"));
    }

    @Test
    public void valuesShouldReturnAllExceptSystemFields() throws JSONException, IOException {
        Child child = new Child();
        child.put("test1", "value1");
        for (Database.ChildTableColumn column : Database.ChildTableColumn.systemFields()) {
            child.put(column.getColumnName(), "test");
        }

        assertThat(child.values(), equalJSONIgnoreOrder("{\"test1\":\"value1\"}"));
    }

    public void shouldReturnListOfChangeLogsBasedOnChanges() throws JSONException {
        Child oldChild = new Child("id", "user", "{'name' : 'old-name'}");
        Child updatedChild = new Child("id", "user", "{'name' : 'updated-name'}");
        List<Child.History> histories = updatedChild.changeLogs(oldChild);

        JSONObject changesMap = (JSONObject) histories.get(0).get(CHANGES);
        HashMap fromTo = (HashMap) changesMap.get("name");

        assertThat(histories.size() ,is(1));
        assertThat(histories.get(0).get(USER_NAME).toString(), is(updatedChild.getOwner()));
        assertThat(changesMap.names().get(0).toString(), is("name"));
        assertThat(fromTo.get(FROM).toString(), is("old-name"));
        assertThat(fromTo.get(TO).toString(), is("updated-name"));
    }

    @Test
    public void shouldReturnListOfChangeLogsForNewFieldValueToExistingChild() throws JSONException {
        Child oldChild = new Child("id", "user", "{'gender' : 'male', 'name' : 'old-name', 'created_organisation' : 'XYZ'}");
        Child updatedChild = new Child("id", "user", "{'gender' : 'male','nationality' : 'Indian', 'name' : 'new-name'}");
        List<Child.History> histories = updatedChild.changeLogs(oldChild);

        JSONObject changesMap = (JSONObject) histories.get(0).get(CHANGES);
        JSONObject fromTo = (JSONObject) changesMap.get("nationality");

        assertThat(histories.size(),is(2));
        assertThat(changesMap.names().get(0).toString(), is("nationality"));
        assertThat(fromTo.get(FROM).toString(),is(""));
        assertThat(fromTo.get(TO).toString(), is("Indian"));

        changesMap = (JSONObject) histories.get(1).get(CHANGES);
        fromTo = (JSONObject) changesMap.get("name");

        assertThat(changesMap.names().get(0).toString(), is("name"));
        assertThat(fromTo.get(FROM).toString(),is("old-name"));
        assertThat(fromTo.get(TO).toString(), is("new-name"));
    }

    @Test
    public void shouldBeNewIfThereIsNoID() throws JSONException {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        assertThat(child.isNew(), is(true));
    }

    @Test
    public void shouldNotBeNewIfThereIsID() throws JSONException {
        Child child = new Child();
        child.put(internal_id.getColumnName(), "xyz");
        assertThat(child.isNew(), is(false));
    }
    
}
