package com.rapidftr.model;


import com.rapidftr.CustomTestRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
        assertFalse(child.isValid());

        child.put("test1", "value1");
        assertTrue(child.isValid());

        child.remove("test1");
        assertFalse(child.isValid());
    }

    @Test
    public void shouldReturnNamesAsEmptyListInsteadOfNull() {
        Child child = new Child();
        assertThat(child.names().length(), equalTo(0));
    }

    @Test
    public void shouldRemoveFieldIfBlank() throws JSONException {
        Child child = new Child();
        child.put("name", "test");
        assertThat(child.names().length(), equalTo(1));

        child.put("name", "\r  \n  \r  \n");
        assertThat(child.names().length(), equalTo(0));
    }

    @Test
    public void shouldRemoveFieldIfNull() throws JSONException {
        Child child = new Child();
        child.put("name", "test");
        assertThat(child.names().length(), equalTo(1));

        Object value = null;
        child.put("name", value);
        assertThat(child.names().length(), equalTo(0));
    }

    @Test
    public void shouldRemoveFieldIfJSONArrayIsEmtpy() throws JSONException {
        Child child = new Child();
        child.put("name", new JSONArray(Arrays.asList("one")));
        assertThat(child.names().length(), equalTo(1));

        child.put("name", new JSONArray());
        assertThat(child.names().length(), equalTo(0));
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

}
