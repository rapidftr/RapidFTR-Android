package com.rapidftr.utils;

import com.rapidftr.model.History;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static JSONArray asJSONObjectArray(List<History> list) throws JSONException {
        JSONArray array = new JSONArray();
        for (JSONObject item : list)
            array.put(item);
        return array;
    }

}
