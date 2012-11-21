package com.rapidftr.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JSONMatcher {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static Matcher<JSONObject> equalJSONIgnoreOrder(final Object value) throws IOException {
        final String content = value.toString().replace('\'', '\"');
        final JsonNode expectedValue = JSON_MAPPER.readTree(content);

        return new CustomMatcher<JSONObject>(content) {
            @Override
            public boolean matches(Object object) {
                try {
                    return JSON_MAPPER.readTree(object.toString()).equals(expectedValue);
                } catch (IOException e) {
                    return false;
                }
            }
        };
    }

    public static final <T extends List<? extends JSONObject>> Matcher<T> hasJSONObjects(Object... expectedObjects) {
        final List<Object> expected = Arrays.asList(expectedObjects);

        return new CustomMatcher<T>(expected.toString()) {

            @Override
            public boolean matches(Object o) {
                try {
                    T actuals = (T) o;
                    boolean matches = expected.size() == actuals.size();

                    if (matches)
                        for (int i=0, j=actuals.size(); matches && i<j; i++)
                            matches = equalJSONIgnoreOrder(expected.get(i)).matches(actuals.get(i));

                    return matches;
                } catch (Exception e) {
                    return false;
                }
            }

        };
    }

}
