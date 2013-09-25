package com.rapidftr.model;

import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class BaseModelTest {

    @Test
    public void shouldDecodeIDFromJSON() throws JSONException {
        BaseModel child = new BaseModel("{ 'unique_identifier' : 'test1' }");
        assertThat(child.getUniqueId(), is("test1"));
    }

    @Test
    public void shouldDecodeString() throws JSONException {
        BaseModel child = new BaseModel("{ 'test1' :  'value1' }");
        assertThat(child.getString("test1"), is("value1"));
    }

    @Test
    public void shouldDecodeOwnerFromJSON() throws JSONException {
        BaseModel child = new BaseModel("{ 'created_by' : 'test1' }");
        assertThat(child.getOwner(), is("test1"));
    }

    @Test
    public void shouldDecodeInteger() throws JSONException {
        BaseModel child = new BaseModel("{ 'test1' : 17 }");
        assertThat(child.getInt("test1"), is(17));
    }

    @Test
    public void shouldDecodeArrayOfStrings() throws JSONException {
        BaseModel child = new BaseModel("{ 'test1' : ['value1', 'value2', 'value3' ]}");
        assertThat(child.getJSONArray("test1").toString(), is(new JSONArray(Arrays.asList("value1", "value2", "value3")).toString()));
    }

    @Test
    public void shouldGenerateWithIdAndOwnerAndContent() throws JSONException {
        BaseModel child = new BaseModel("id1", "owner1", "{ 'test1' : 'value1' }");
        assertThat(child.getUniqueId(), is("id1"));
        assertThat(child.getOwner(), is("owner1"));
        assertThat(child.getString("test1"), is("value1"));
    }

    @Test
    public void shouldNotOverwriteCreatedAtIfGiven() throws JSONException {
        BaseModel child = new BaseModel(String.format("{ 'created_at' :  '%s' }", new RapidFtrDateTime(10, 2, 2012).defaultFormat()));
        assertThat(child.getCreatedAt(), is("2012-02-10 00:00:00"));
    }

    @Test
    public void shouldGenerateUniqueId() throws JSONException {
        BaseModel child = new BaseModel(null, "rapidftr", null);
        child = spy(child);

        doReturn("xyz").when(child).createUniqueId();

        child.generateUniqueId();
        assertThat(child.getUniqueId(), equalTo("xyz"));
    }

    @Test
    public void shouldNotOverwriteIdIfAlreadyPresent() throws JSONException {
        BaseModel child = new BaseModel("id1", "owner1", null);
        child.generateUniqueId();
        assertThat(child.getUniqueId(), equalTo("id1"));
    }

    @Test
    public void shouldRemoveFromJSONArray() throws JSONException {
        BaseModel child = new BaseModel("{ 'test1' : ['value1', 'value2', 'value3' ]}");
        child.removeFromJSONArray("test1", "value1");
        assertThat(child.getJSONArray("test1").toString(), is(new JSONArray(Arrays.asList("value2", "value3")).toString()));
    }

    @Test
    public void shouldRemoveFieldIfJSONArrayIsEmtpy() throws JSONException {
        BaseModel child = new Child();
        child.put("name", new JSONArray(Arrays.asList("one")));
        assertThat(child.values().names().length(), equalTo(1));

        child.put("name", new JSONArray());
        assertNull(child.values().names());
    }

    @Test
    public void shouldRemoveFieldIfNull() throws JSONException {
        BaseModel child = new BaseModel();
        child.put("name", "test");

        assertEquals(child.get("name"), "test");

        Object value = null;
        child.put("name", value);
        assertNull(child.opt("name"));
    }


}
