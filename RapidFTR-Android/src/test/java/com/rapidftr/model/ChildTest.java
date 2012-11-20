package com.rapidftr.model;


import com.rapidftr.CustomTestRunner;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.rapidftr.model.Child.History.*;
import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
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
        Child child = new Child("{ '_id' : 'test1' }");
        assertThat(child.getId(), is("test1"));
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
        assertThat(child.getId(), is("id1"));
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

        doReturn("xyz").when(child).createUniqueId(any(Calendar.class));

        child.generateUniqueId();
        assertThat(child.getId(), equalTo("xyz"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateUniqueIdIfOwnerIsNull() throws JSONException {
        new Child().generateUniqueId();
    }

    @Test
    public void shouldNotOverwriteIdIfAlreadyPresent() throws JSONException {
        Child child = new Child("id1", "owner1", null);
        child.generateUniqueId();
        assertThat(child.getId(), equalTo("id1"));
    }

    @Test
    public void testShouldGenerateUniqueIdInFormat() throws JSONException {
        Child child = new Child(null, "rapidftr", null);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);

        String guid = child.createUniqueId(calendar);

        assertTrue(guid.contains("rapidftr20120721"));
        assertTrue(guid.length() == 21);
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
    public void shouldReturnNamesWithLengthOneInsteadOfNull() {
        Child child = new Child();
        assertThat(child.names().length(), equalTo(1));
    }

    @Test
    public void shouldRemoveFieldIfBlank() throws JSONException {
        Child child = new Child();
        child.put("name", "test");
        assertThat(child.names().length(), equalTo(2));

        child.put("name", "\r  \n  \r  \n");
        assertThat(child.names().length(), equalTo(1));
    }

    @Test
    public void shouldHaveFalseSyncStatusIfTheChildObjectIsCreated(){
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
        assertThat(child.names().length(), equalTo(2));

        child.put("name", new JSONArray());
        assertThat(child.names().length(), equalTo(1));
    }

    @Test
    public void shouldTrimFieldValues() throws JSONException {
        Child child = new Child();
        child.put("name", "\r\n line1 \r\n line2 \r\n \r\n");
        assertThat(child.getString("name"), equalTo("line1 \r\n line2"));
    }

    @Test
    public void shouldNotGenerateDuplicateIdForSameUserSameChildNameAndSameDate() throws JSONException {
        Child child = new Child(null, "rapidftr", null);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);

        String guid1 = child.createUniqueId(calendar);
        String guid2 = child.createUniqueId(calendar);

        assertNotSame(guid1, guid2);
    }
    
    @Test
    public void shouldReturnValueFromJSON() throws JSONException {
        Child child = new Child("{ 'created_by' : 'test1' }");
        assertEquals("test1",child.getFromJSON("created_by"));
        assertNull("test1",child.getFromJSON("unknown_key"));
    }

    @Test
    public void shouldReturnListOfChangeLogsBasedOnChanges() throws JSONException {
        Child oldChild = new Child("id", "user", "{'name' : 'old-name'}");
        Child updatedChild = new Child("id", "user", "{'name' : 'updated-name'}");
        List<Child.History> histories = updatedChild.changeLogs(oldChild);

        assertThat(histories.size() ,is(1));
        assertThat(histories.get(0).get(USER_NAME).toString(), is(updatedChild.getOwner()));
        assertThat(histories.get(0).get(FIELD_NAME).toString(), is("name"));
        assertThat(histories.get(0).get(FROM).toString(), is("old-name"));
        assertThat(histories.get(0).get(TO).toString(), is("updated-name"));
    }

    @Test
    public void shouldReturnListOfChangeLogsForNewFieldValueToExistingChild() throws JSONException {
        Child oldChild = new Child("id", "user", "{'gender' : 'male', 'name' : 'old-name'}");
        Child updatedChild = new Child("id", "user", "{'gender' : 'male','nationality' : 'Indian', 'name' : 'new-name'}");
        List<Child.History> histories = updatedChild.changeLogs(oldChild);

        assertThat(histories.size(),is(2));
        assertThat(histories.get(0).get(FIELD_NAME).toString(), is("nationality"));
        assertThat(histories.get(0).get(FROM).toString(),is(""));
        assertThat(histories.get(0).get(TO).toString(), is("Indian"));

        assertThat(histories.get(1).get(FIELD_NAME).toString(), is("name"));
        assertThat(histories.get(1).get(FROM).toString(), is("old-name"));
        assertThat(histories.get(1).get(TO).toString(), is("new-name"));
    }

}
