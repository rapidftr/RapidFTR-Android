package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.History;
import com.rapidftr.utils.JSONArrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.rapidftr.model.History.HISTORIES;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
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
    public void shouldCreateChildRecordAndSetCreatedAtHistory() throws Exception {
        Enquiry enquiry = new Enquiry("{\"age\":14,\"name\":\"Subhas\"}", user);
        enquiryRepository.createOrUpdate(enquiry);
        JSONObject enquiryJsonValues = enquiryRepository.get(enquiry.getUniqueId()).values();
        JSONArray histories = (JSONArray) enquiryJsonValues.get(History.HISTORIES);
        JSONObject changes = (JSONObject) ((JSONObject) histories.get(0)).get("changes");
        assert(((JSONObject) changes.get("enquiry")).has(History.CREATED));
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
    public void shouldReturnAllEnquiriesCreatedByUser() throws JSONException {
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
    @Ignore
    public void getShouldReturnEnquiryForId() throws Exception {
        Enquiry enquiry1 = new Enquiry("{age:14,name:Subhas}", user);
        String enquiryId = enquiry1.getUniqueId();

        enquiryRepository.createOrUpdate(enquiry1);

        assertEquals(enquiryRepository.get(enquiryId).toString(), enquiry1.toString());
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
        enquiryRepository.createOrUpdateWithoutHistory(enquiry);

        Enquiry retrieved = enquiryRepository.all().get(0);

        assertThat(retrieved.optString("enquirer_name"), is("New Reporter Name"));
        assertThat(retrieved.getCreatedBy().toString(), is("NEW USER"));
        assertTrue(retrieved.isSynced());
        assertThat(retrieved.getString(Database.EnquiryTableColumn.internal_id.getColumnName()), is("new internal id"));
        assertThat(retrieved.getString(Database.EnquiryTableColumn.internal_rev.getColumnName()), is("new internal revision"));
    }

    @Test
    public void createOrUpdateShouldAddHistory() throws Exception {
        Enquiry enquiry = new Enquiry("{\n" +
                "\"createdBy\":\"user\",\n" +
                "\"enquirer_name\":\"faris\",\n" +
                "\"criteria\":{\"age\":14, \"name\": \"Subhas\"},\n" +
                "\"synced\":\"true\",\n" +
                "\"created_by\":\"some guy\"" +
                "}");
        enquiryRepository.createOrUpdate(enquiry);
        enquiry.put("enquirer_name", "New Reporter Name");
        enquiryRepository.createOrUpdate(enquiry);

        JSONObject enquiryJsonValues = enquiryRepository.get(enquiry.getUniqueId()).values();
        assertTrue(enquiryJsonValues.has(History.HISTORIES));
    }

    @Test
    public void updateShouldAddHistory() throws Exception {
        Enquiry enquiry = new Enquiry("{\n" +
                "\"createdBy\":\"user\",\n" +
                "\"enquirer_name\":\"faris\",\n" +
                "\"criteria\":{\"age\":14, \"name\": \"Subhas\"},\n" +
                "\"synced\":\"true\",\n" +
                "\"created_by\":\"some guy\"" +
                "}");
        enquiryRepository.createOrUpdate(enquiry);
        enquiry.put("enquirer_name", "New Reporter Name");
        enquiryRepository.createOrUpdate(enquiry);

        JSONObject enquiryJsonValues = enquiryRepository.get(enquiry.getUniqueId()).values();
        assertTrue(enquiryJsonValues.has(History.HISTORIES));
    }

    @Test(expected = android.database.SQLException.class)
    public void shouldReturnFailedToSaveEnquiryExceptionWhenSavingEnquiryWithData() throws
            SQLException, JSONException {

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
    public void shouldAnEnquiryUsingInternalIds() throws JSONException {
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
    public void shouldReturnAllWithInternalIds() throws JSONException {
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
    public void shouldCreateNewEnquiryWithoutHistory() throws JSONException {
        Enquiry enquiry = new Enquiry("{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_3\",\n" +
                "\"created_by\":\"some guy\"" +
                "}");

        enquiryRepository.createOrUpdateWithoutHistory(enquiry);

        Enquiry savedEnquiry = enquiryRepository.get(enquiry.getUniqueId());
        assertNotNull(savedEnquiry);
        assertFalse(savedEnquiry.has(HISTORIES));
    }

    @Test
    public void shouldUpdateExistingChildWithoutHistory() throws JSONException {
        Enquiry enquiry = new Enquiry("{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_3\",\n" +
                "\"created_by\":\"some guy\"" +
                "}");        enquiryRepository.createOrUpdateWithoutHistory(enquiry);
        enquiry.put("more_stuff", "some_more_stuff");
        enquiryRepository.createOrUpdateWithoutHistory(enquiry);

        Enquiry savedEnquiry = enquiryRepository.get(enquiry.getUniqueId());

        assertNotNull(savedEnquiry);
        assertFalse(savedEnquiry.has(HISTORIES));
        assertEquals("some_more_stuff", savedEnquiry.get("more_stuff"));
    }
}
