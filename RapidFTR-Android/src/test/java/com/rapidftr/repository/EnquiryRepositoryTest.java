package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Enquiry;
import com.rapidftr.utils.JSONArrays;
import com.rapidftr.utils.RapidFtrDateTime;
import org.hamcrest.CoreMatchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.sql.SQLException;
import java.util.*;

import static com.rapidftr.model.BaseModel.History.*;
import static com.rapidftr.model.BaseModel.History.CHANGES;
import static com.rapidftr.model.BaseModel.History.HISTORIES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(CustomTestRunner.class)
public class EnquiryRepositoryTest {

    private DatabaseSession session;
    private EnquiryRepository enquiryRepository;
    private String user = "field worker";

    @Before
    public void setUp() {
        session = new ShadowSQLiteHelper("test_database").getSession();
        enquiryRepository = new EnquiryRepository(user, session);
    }

    @Ignore
    public void shouldCreateAnEnquiryInTheDatabase() throws JSONException, SQLException {
        Enquiry enquiry = new Enquiry("{\"age\":14,\"name\":\"Subhas\"}", user);
        enquiryRepository.createOrUpdate(enquiry);
        assertEquals(1, enquiryRepository.size());

        final Cursor cursor = session.rawQuery("SELECT criteria FROM enquiry WHERE id = ?", new String[]{enquiry.getUniqueId()});
        cursor.moveToNext();

        JSONObject criteria = new JSONObject(cursor.getString(0));
        assertThat(criteria.length(), is(2));
        assertThat(criteria.getInt("age"), is(14));
        assertThat(criteria.getString("name"), is("Subhas"));
    }

    @Test
    public void shouldReturnAllEnquiries() throws Exception {
        String enquiryJSON1 = "{\"enquirer_name\":\"sam fisher\",\"name\":\"foo bar\",\"nationality\":\"ugandan\"," +
                "\"created_by\":\"Tom Reed\",\"synced\":\"false\", \"created_organisation\":\"TW\"}";
        String enquiryJSON2 = "{\"enquirer_name\":\"fisher sam\",\"name\":\"bar foo\",\"nationality\":\"ugandan\"," +
                "\"created_by\":\"Tom Reed\",\"synced\":\"false\", \"created_organisation\":\"TW\"}";
        Enquiry enquiry1 = new Enquiry(enquiryJSON1);
        enquiryRepository.createOrUpdate(enquiry1);
        Enquiry enquiry2 = new Enquiry(enquiryJSON2);
        enquiryRepository.createOrUpdate(enquiry2);

        List<Enquiry> enquiries = enquiryRepository.all();
        assertEquals(2, enquiryRepository.size());
        compareEnquiries(enquiry1, enquiries.get(0));
        compareEnquiries(enquiry2, enquiries.get(1));
    }

    @Test
    public void shouldReturnAllEnquiriesCreatedByUser() throws JSONException, FailedToSaveException {
        String enquiryJSON1 = "{\"enquirer_name\":\"sam fisher\",\"name\":\"foo bar\",\"nationality\":\"ugandan\"," +
                "\"created_by\":\"field worker\",\"synced\":\"false\", \"created_organisation\":\"TW\"}";
        String enquiryJSON2 = "{\"enquirer_name\":\"fisher sam\",\"name\":\"bar foo\",\"nationality\":\"ugandan\"," +
                "\"created_by\":\"Tom Reed\",\"synced\":\"false\", \"created_organisation\":\"TW\"}";
        Enquiry enquiry1 = new Enquiry(enquiryJSON1);
        enquiryRepository.createOrUpdate(enquiry1);
        Enquiry enquiry2 = new Enquiry(enquiryJSON2);
        enquiryRepository.createOrUpdate(enquiry2);

        List<Enquiry> enquiries = enquiryRepository.allCreatedByCurrentUser();
        assertEquals(2, enquiryRepository.size());
        compareEnquiries(enquiry1, enquiries.get(0));
    }

    private void compareEnquiries(Enquiry enquiry1, Enquiry enquiry2) throws JSONException {
        assertThat(enquiry1.getUniqueId(), is(enquiry2.getUniqueId()));
        assertThat(enquiry1.getCreatedBy(), is(enquiry2.getCreatedBy()));
        assertThat(enquiry1.getCreatedAt(), is(enquiry2.getCreatedAt()));
    }

    @Test
    public void getShouldReturnEnquiryForId() throws Exception {
        Enquiry enquiry1 = new Enquiry("{age:14,name:Subhas}", user);
        String enquiryId = enquiry1.getUniqueId();

        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry savedEnquiry = enquiryRepository.get(enquiryId);
        savedEnquiry.remove("id");
        JSONAssert.assertEquals(savedEnquiry.toString(), enquiry1.toString(), false);
    }

    @Test
    public void toBeSyncedShouldReturnAListOfEnquiriesWithSyncedStatusFalse() throws Exception {
        Enquiry enquiry1 = new Enquiry("{age:14,name:Subhas}", user);
        enquiry1.setSynced(false);
        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry enquiry2 = new Enquiry("{age:14,name:Subhas}", "field worker 2");
        enquiry2.setSynced(true);
        enquiryRepository.createOrUpdate(enquiry2);

        final List<Enquiry> enquiries = enquiryRepository.toBeSynced();

        assertThat(enquiries.size(), is(1));
        assertThat(enquiries.get(0).getUniqueId(), is(enquiry1.getUniqueId()));
    }

    @Test
    public void existsShouldReturnTrueGivenAnIdOfAnEnquiryThatAlreadyExists() throws Exception {
        Enquiry enquiry1 = new Enquiry("{age:14,name:Subhas}", user);
        enquiryRepository.createOrUpdate(enquiry1);

        assertTrue(enquiryRepository.exists(enquiry1.getUniqueId()));
        assertFalse(enquiryRepository.exists("100"));
    }

    @Test
    public void getAllIdsAndRevs_shouldReturnIdsAndRevsForAllEnquiries() throws Exception {
        Enquiry enquiry1 = new Enquiry("{age:14,name:Subhas}", user);
        String enquiry1CouchId = UUID.randomUUID().toString();
        String enquiry1CouchRev = UUID.randomUUID().toString();
        enquiry1.put("_id", enquiry1CouchId);
        enquiry1.put("_rev", enquiry1CouchRev);
        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry enquiry2 = new Enquiry("{age:14,name:Subhas}", "field worker 2");
        String enquiry2CouchId = UUID.randomUUID().toString();
        String enquiry2CouchRev = UUID.randomUUID().toString();
        enquiry2.put("_id", enquiry2CouchId);
        enquiry2.put("_rev", enquiry2CouchRev);
        enquiryRepository.createOrUpdate(enquiry2);

        final HashMap<String, String> allIdsAndRevs = enquiryRepository.getAllIdsAndRevs();

        assertThat(allIdsAndRevs.size(), is(2));
        assertThat(allIdsAndRevs.get(enquiry1CouchId), is(enquiry1CouchRev));
        assertThat(allIdsAndRevs.get(enquiry2CouchId), is(enquiry2CouchRev));
    }

    @Test
    public void updateShouldUpdateTheFieldsOfAnEnquiry() throws Exception {
        String enquiryJSON = "{\n" +
                "\"createdBy\":\"user\",\n" +
                "\"enquirer_name\":\"faris\",\n" +
                "\"criteria\":{\"age\":14, \"name\": \"Subhas\"},\n" +
                "\"synced\":\"true\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";

        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepository.createOrUpdate(enquiry);

        assertThat(enquiryRepository.all().size(), is(1));

        enquiry.put("enquirer_name", "New Reporter Name");
        enquiry.setCreatedBy("NEW USER");
        enquiry.setSynced(true);
        enquiry.put(Database.EnquiryTableColumn.internal_id.getColumnName(), "new internal id");
        enquiry.put(Database.EnquiryTableColumn.internal_rev.getColumnName(), "new internal revision");
        enquiryRepository.update(enquiry);

        Enquiry retrieved = enquiryRepository.all().get(0);

        assertThat(retrieved.optString("enquirer_name"), is("New Reporter Name"));
        assertThat(retrieved.getCreatedBy().toString(), is("NEW USER"));
        assertTrue(retrieved.isSynced());
        assertThat(retrieved.getString(Database.EnquiryTableColumn.internal_id.getColumnName()), is("new internal id"));
        assertThat(retrieved.getString(Database.EnquiryTableColumn.internal_rev.getColumnName()), is("new internal revision"));
    }

    @Test(expected = FailedToSaveException.class)
    public void shouldReturnFailedToSaveEnquiryExceptionWhenSavingEnquiryWithData() throws
            FailedToSaveException, JSONException {

        String enquiryJSON = "{\n" +
                "\"created_by\":\"user\",\n" +
                "\"synced\":\"true\",\n" +
                "}";

        ContentValues contentValues = new ContentValues();

        contentValues.put("created_by", "some user");

        Enquiry enquiry = new Enquiry(enquiryJSON);
        EnquiryRepository enquiryRepository1 = spy(enquiryRepository);

        when(enquiryRepository1.getContentValuesFrom(enquiry)).thenReturn(contentValues);

        enquiryRepository1.createOrUpdate(enquiry);
    }

    @Test
    public void shouldAnEnquiryUsingInternalIds() throws JSONException, FailedToSaveException {
        String enquiryJSON = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_1\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepository.createOrUpdate(enquiry);

        List<String> ids = new ArrayList<String>();
        ids.add("enquiry_id_1");
        List<Enquiry> enquiries = enquiryRepository.getAllWithInternalIds(ids);
        assertThat(enquiries.size(), is(1));
        assertThat(enquiries.get(0).getUniqueId(), is(enquiry.getUniqueId()));
    }

    @Test
    public void shouldReturnAllWithInternalIds() throws JSONException, FailedToSaveException {
        String enquiryJSON = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_1\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry1 = new Enquiry(enquiryJSON);
        enquiryRepository.createOrUpdate(enquiry1);

        String enquiryJSON2 = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_2\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry2 = new Enquiry(enquiryJSON2);
        enquiryRepository.createOrUpdate(enquiry2);

        String enquiryJSON3 = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_3\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry3 = new Enquiry(enquiryJSON3);
        enquiryRepository.createOrUpdate(enquiry3);

        List<String> ids = new ArrayList<String>();
        ids.add("enquiry_id_1");
        ids.add("enquiry_id_3");
        List<Enquiry> enquiries = enquiryRepository.getAllWithInternalIds(ids);
        assertThat(enquiries.size(), is(2));
        assertThat(enquiries.get(0).getUniqueId(), is(enquiry1.getUniqueId()));
        assertThat(enquiries.get(1).getUniqueId(), is(enquiry3.getUniqueId()));
    }

    @Test
    public void shouldAddHistoriesIfEnquiryHasBeenUpdated() throws JSONException, FailedToSaveException {
        Enquiry existingEnquiry = new Enquiry("{'name' : 'old-name', 'unique_identifier' : '1'}", "user1");
        enquiryRepository.createOrUpdate(existingEnquiry);

        Enquiry updatedEnquiry = new Enquiry("{'name' : 'updated-name',  'unique_identifier' : '1'}", "user1");
        Enquiry spyUpdatedEnquiry = spy(updatedEnquiry);
        List<BaseModel.History> histories = new ArrayList<BaseModel.History>();
        BaseModel.History updatedEnquiryHistory = updatedEnquiry.new History();
        HashMap changes = new HashMap();
        HashMap fromTo = new HashMap();
        fromTo.put(FROM, "old-name");
        fromTo.put(TO, "new-name");
        changes.put("name", fromTo);
        updatedEnquiryHistory.put(USER_NAME, "user");
        updatedEnquiryHistory.put(DATETIME, "timestamp");
        updatedEnquiryHistory.put(CHANGES, changes);
        histories.add(updatedEnquiryHistory);

        doReturn(histories).when(spyUpdatedEnquiry).changeLogs(any(Enquiry.class), any(JSONArray.class));
        enquiryRepository.createOrUpdate(spyUpdatedEnquiry);
        Enquiry savedEnquiry = enquiryRepository.get(updatedEnquiry.getUniqueId());
        assertThat(savedEnquiry.get(HISTORIES).toString(), CoreMatchers.is("[{\"user_name\":\"user\",\"datetime\":\"timestamp\",\"changes\":{\"name\":{\"to\":\"new-name\",\"from\":\"old-name\"}}}]"));
    }

    @Test
    public void shouldMergeHistoriesIfHistoriesAlreadyExist() throws JSONException, FailedToSaveException {

        String olderHistoryLastSavedAt = new RapidFtrDateTime(1, 2, 2012).defaultFormat();
        String last_synced_at = new RapidFtrDateTime(1, 2, 2013).defaultFormat();
        String last_saved_at = new RapidFtrDateTime(2, 2, 2013).defaultFormat();

        String oldHistories = String.format("[{\"user_name\":\"user\",\"datetime\":\"%s\",\"changes\":{\"rc_id_no\":{\"from\":\"old_rc_id\",\"to\":\"new_rc_id\"}}}, {\"user_name\":\"user\",\"datetime\":\"%s\",\"changes\":{\"name\":{\"from\":\"old-name\",\"to\":\"new-name\"}}}]", olderHistoryLastSavedAt, last_saved_at);
        String oldContent = String.format("{'last_synced_at':'%s','gender' : 'male','nationality' : 'Indian', 'name' : 'new-name', 'separated': 'yes', 'rc_id_no': '1234'}", last_synced_at);
        Enquiry enquiryWithHistories = new Enquiry(oldContent, "user");
        enquiryWithHistories.put(BaseModel.History.HISTORIES, new JSONArray(oldHistories));
        enquiryRepository.createOrUpdate(enquiryWithHistories);

        enquiryWithHistories.put("name", "updated-name");
        enquiryWithHistories.put("separated", "no");
        enquiryWithHistories.put("protected", "yes");

        enquiryRepository.createOrUpdate(enquiryWithHistories);
        List<Object> enquiryHistories = JSONArrays.asList((JSONArray) enquiryWithHistories.get("histories"));
        assertThat(enquiryHistories.size(), CoreMatchers.is(2));
        JSONObject jsonObject = (JSONObject) enquiryHistories.get(1);

        assertOnHistory(jsonObject, "name", "updated-name", "new-name");
        assertOnHistory(jsonObject, "separated", "no", "yes");
        assertOnHistory(jsonObject, "protected", "yes", "");
    }

    private void assertOnHistory(JSONObject jsonObject, String name, String toString, String fromString) throws JSONException {
        JSONObject changesKey = (JSONObject) ((JSONObject) jsonObject.get("changes")).get(name);
        assertThat(changesKey.optString("to"), CoreMatchers.is(toString));
        assertThat(changesKey.optString("from"), CoreMatchers.is(fromString));
    }

    @Test
    public void shouldConstructTheHistoryObjectIfHistoriesArePassedAsStringInContent() throws JSONException, FailedToSaveException {
        Enquiry enquiry = new Enquiry("{\"histories\":[{\"changes\":{\"name\":{\"from\":\"old-name\",\"to\":\"new-name\"}}}, {\"changes\":{\"sex\":{\"from\":\"\",\"to\":\"male\"}}}]}", "user1");
        enquiryRepository.createOrUpdate(enquiry);
        List<Enquiry> enquiries = enquiryRepository.toBeSynced();
        JSONArray histories = (JSONArray) enquiries.get(0).get(HISTORIES);
        assertThat(histories.length(), CoreMatchers.is(2));

        JSONObject name = (JSONObject) ((JSONObject) ((JSONObject) histories.get(0)).get("changes")).get("name");
        JSONObject sex = (JSONObject) ((JSONObject) ((JSONObject) histories.get(1)).get("changes")).get("sex");

        assertThat(name.get("from").toString(), CoreMatchers.is("old-name"));
        assertThat(name.get("to").toString(), CoreMatchers.is("new-name"));
        assertThat(sex.get("from").toString(), CoreMatchers.is(""));
        assertThat(sex.get("to").toString(), CoreMatchers.is("male"));
    }


    @Test
    public void shouldCompareWithLastSyncedAtDateBeforeGeneratingChangeLogs() throws JSONException, FailedToSaveException {

        String last_synced_at = new RapidFtrDateTime(1, 2, 2013).defaultFormat();
        String last_saved_at = new RapidFtrDateTime(2, 2, 2013).defaultFormat();

        String oldHistories = String.format("[{\"user_name\":\"user\",\"datetime\":\"%s\",\"changes\":{\"name\":{\"from\":\"old-name\",\"to\":\"new-name\"}}}]", last_saved_at);
        String oldContent = String.format("{'last_synced_at':'%s','gender' : 'male','nationality' : 'Indian', 'name' : 'new-name', 'separated': 'yes', 'rc_id_no': '1234', 'histories' : %s}", last_synced_at, oldHistories);
        Enquiry enquiryWithHistories = new Enquiry(oldContent, "user");
        enquiryRepository.createOrUpdate(enquiryWithHistories);

        String newContent = String.format("{'last_synced_at':'%s','gender' : 'female','nationality' : 'Indian', 'name' : 'new-name', 'rc_id_no': '1234', 'separated' : 'true', 'histories' : %s}", last_synced_at, oldHistories);
        Enquiry updatedEnquiry = new Enquiry(newContent, "user");

        enquiryRepository.createOrUpdate(updatedEnquiry);
        assertThat(((JSONArray) updatedEnquiry.get(HISTORIES)).length(), CoreMatchers.is(1));
    }

}
