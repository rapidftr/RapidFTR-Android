package com.rapidftr.utils;

import com.rapidftr.CustomTestRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(CustomTestRunner.class)
public class JSONArraysTest {

    @Test
    public void testConvertJSONArrayToList() throws JSONException {
        JSONArray array = new JSONArray("[ '1', '2', '3' ]");
        List<Object> list = JSONArrays.asList(array);
        assertThat(list, equalTo(Arrays.asList(new Object[] { "1", "2", "3" })));
    }

    @Test
    public void testConvertListToJSONArray() throws JSONException {
        List<Object> list = Arrays.asList(new Object[] { "1", "2", "3" });
        JSONArray array = JSONArrays.asJSONArray(list);

        assertThat(array.toString(), equalTo("[\"1\",\"2\",\"3\"]"));
    }

}
