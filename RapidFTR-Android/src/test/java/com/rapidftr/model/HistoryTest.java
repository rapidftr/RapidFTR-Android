package com.rapidftr.model;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.RapidFtrDateTime;
import junit.framework.TestCase;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.ParseException;
import java.util.Calendar;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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
        RapidFtrApplication.getApplicationInstance().setCurrentUser(new User("user_name"));

        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"deletion\":\"old stuff\",\"change2\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"new stuff\",\"change2\":\"Foo Bar2\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        assertEquals("user_name", history.getString(History.USER_NAME));
    }

    @Test
    public void shouldAddUserOrganisationToHistory() throws JSONException {
        User user = new User("user_name");
        user.setOrganisation("UNICEF");
        RapidFtrApplication.getApplicationInstance().setCurrentUser(user);

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

    @Test
    public void shouldNotIncludeRemovingEmptyValuesInHistory() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"unique_identifier\":\"1\",\"deletionz\":\"\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        JSONObject changes = (JSONObject) history.get(History.CHANGES);
        assertFalse(changes.has("deletion"));
    }

    @Test
    public void shouldNotIncludeAddingEmptyValuesInHistory() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        JSONObject changes = (JSONObject) history.get(History.CHANGES);
        assertFalse(changes.has("addition"));
    }

    @Test
    public void shouldNotIncludeEmptyChangesInHistory() throws JSONException {
        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"addition\":\"\",\"unique_identifier\":\"1\"}");
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"\",\"unique_identifier\":\"1\"}");

        History history = History.buildHistoryBetween(originalModel, changedModel);
        JSONObject changes = (JSONObject) history.get(History.CHANGES);
        assertFalse(changes.has("addition"));
    }

    @Test
    public void shouldNotIncludeHistoryInChanges() throws JSONException, ParseException {
        BaseModel originalModel = new BaseModel("{\"change1\":\"Foo Bar\",\"deletion\":\"old stuff\",\"change2\":\"Foo Bar\",\"unique_identifier\":\"1\"}");
        originalModel.addHistory(new History("{\"changes\":{\"change1\":{\"from\":\"Foo Bar\",\"to\":\"old stuff\"}}}"));
        BaseModel changedModel = new BaseModel("{\"change1\":\"Foo Bar1\",\"addition\":\"new stuff\",\"change2\":\"Foo Bar2\",\"unique_identifier\":\"1\"}");
        changedModel.addHistory(new History("{\"changes\":{\"something_else\":{\"from\":\"Foo Bar\",\"to\":\"old stuff\"}}}"));

        History history = History.buildHistoryBetween(originalModel, changedModel);

        JSONObject changes = (JSONObject) history.get(History.CHANGES);
        assertFalse(changes.has(History.HISTORIES));
        assertTrue(changedModel.has(History.HISTORIES));
        assertTrue(originalModel.has(History.HISTORIES));
    }
}
