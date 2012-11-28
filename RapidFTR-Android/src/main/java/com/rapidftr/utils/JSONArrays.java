package com.rapidftr.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JSONArrays {

    public static List<Object> asList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>(array.length());

        for (int i=0, j=array.length(); i<j; i++)
            list.add(array.get(i));

        return list;
    }

    public static JSONArray asJSONArray(List<Object> list) throws JSONException {
        JSONArray array = new JSONArray();
        for (Object item : list)
            array.put(item);

        return array;
    }

}
