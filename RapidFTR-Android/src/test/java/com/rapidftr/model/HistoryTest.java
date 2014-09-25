package com.rapidftr.model;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.ParseException;
import java.util.Calendar;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class HistoryTest {

    @Test
    public void shouldCompareObjectsAndReturnHistory() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"child_name\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"child_name\":\"Foo Bar124\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);

        String  expectedJSON = "{\"child_name\":{\"from\":\"Foo Bar\", \"to\":\"Foo Bar124\"}}";
        JSONAssert.assertEquals(expectedJSON, history.getString(History.CHANGES), true);
    }

    @Test
    public void shouldIncludeNewFieldsInHistory() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"child_name\":\"Foo Bar\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);

        String  expectedJSON = "{\"child_name\":{\"from\":\"\", \"to\":\"Foo Bar\"}}";
        JSONAssert.assertEquals(expectedJSON, history.getString(History.CHANGES), true);
    }

    @Test
    public void shouldIncludeDeletedFieldsInHistory() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"child_name\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);

        String  expectedJSON = "{\"child_name\":{\"from\":\"Foo Bar\", \"to\":\"\"}}";
        JSONAssert.assertEquals(expectedJSON, history.getString(History.CHANGES), true);
    }

    @Test
    public void shouldHandleComplexChanges() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"deletion\":\"old stuff\",\"change2\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"new stuff\",\"change2\":\"Foo Bar2\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);

        String  expectedJSON = "{\"change1\":{\"from\":\"Foo Bar\",\"to\":\"Foo Bar1\"}," +
                "\"change2\":{\"from\":\"Foo Bar\",\"to\":\"Foo Bar2\"}," +
                "\"deletion\":{\"from\":\"old stuff\",\"to\":\"\"}," +
                "\"addition\":{\"from\":\"\",\"to\":\"new stuff\"}" +
                "}";
        JSONAssert.assertEquals(expectedJSON, history.getString(History.CHANGES), true);
    }

    @Test
    public void shouldAddUserName() throws JSONException {
        RapidFtrApplication.getApplicationInstance().getSharedPreferences().edit().putString("USER_NAME", "user_name").commit();

        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"deletion\":\"old stuff\",\"change2\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"new stuff\",\"change2\":\"Foo Bar2\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        assertEquals("user_name", history.getString(History.USER_NAME));
    }

    @Test
    public void shouldAddUserOrganisationToHistory() throws JSONException {
        RapidFtrApplication.getApplicationInstance().getSharedPreferences().edit().putString("USER_ORG", "UNICEF").commit();

        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"deletion\":\"old stuff\",\"change2\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"new stuff\",\"change2\":\"Foo Bar2\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        assertEquals("UNICEF", history.getString(History.USER_ORGANISATION));
    }

    @Test
    public void shouldAddDateTimeToHistory() throws JSONException, ParseException {
        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"deletion\":\"old stuff\",\"change2\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"new stuff\",\"change2\":\"Foo Bar2\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        Calendar updatedAt = RapidFtrDateTime.getDateTime(history.getString(History.DATETIME));
        assert(Calendar.getInstance().getTimeInMillis() - updatedAt.getTimeInMillis() < 1000);
    }
}
