package com.rapidftr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
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
        Enquiry enquiry = new Enquiry(user, new JSONObject("{\"age\":14,\"name\":\"Subhas\"}"));
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

    private void compareEnquiries(Enquiry enquiry1, Enquiry enquiry2) throws JSONException {
        assertThat(enquiry1.getUniqueId(), is(enquiry2.getUniqueId()));
        assertThat(enquiry1.getCreatedBy(), is(enquiry2.getCreatedBy()));
        assertThat(enquiry1.getCreatedAt(), is(enquiry2.getCreatedAt()));
    }

    @Test
    @Ignore
    public void getShouldReturnEnquiryForId() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, new JSONObject("{age:14,name:Subhas}"));
        String enquiryId = enquiry1.getUniqueId();

        enquiryRepository.createOrUpdate(enquiry1);

        assertEquals(enquiryRepository.get(enquiryId).toString(), enquiry1.toString());
    }

    @Test
    public void shouldMaintainWellFormedCriteriaWhenEnquiryIsSaved() throws JSONException, SQLException {
        String enquiryJSON = "{ \"enquirer_name\":\"sam fisher\", \"name\":\"foo bar\"," +
                "\"nationality\":\"ugandan\",\"created_by\" : \"Tom Reed\",\"synced\" : \"false\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        String expectedEnquirerName = "sam fisher";
        String expectedCriteria = "{\"name\":\"foo bar\", \"nationality\":\"ugandan\"}";

        enquiryRepository.createOrUpdate(enquiry);

        Enquiry retrievedEnquiry = enquiryRepository.get(enquiry.getUniqueId());
        assertEquals(expectedEnquirerName, retrievedEnquiry.get("enquirer_name"));
        JSONAssert.assertEquals(expectedCriteria, retrievedEnquiry.getCriteria(), true);
    }

    @Test
    public void toBeSyncedShouldReturnAListOfEnquiriesWithSyncedStatusFalse() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, new JSONObject("{age:14,name:Subhas}"));
        enquiry1.setSynced(false);
        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry enquiry2 = new Enquiry("field worker 2", new JSONObject("{age:14,name:Subhas}"));
        enquiry2.setSynced(true);
        enquiryRepository.createOrUpdate(enquiry2);

        final List<Enquiry> enquiries = enquiryRepository.toBeSynced();

        assertThat(enquiries.size(), is(1));
        assertThat(enquiries.get(0).getUniqueId(), is(enquiry1.getUniqueId()));
    }

    @Test
    public void existsShouldReturnTrueGivenAnIdOfAnEnquiryThatAlreadyExists() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry1);

        assertTrue(enquiryRepository.exists(enquiry1.getUniqueId()));
        assertFalse(enquiryRepository.exists("100"));
    }

    @Test
    public void getAllIdsAndRevs_shouldReturnIdsAndRevsForAllEnquiries() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, new JSONObject("{age:14,name:Subhas}"));
        String enquiry1CouchId = UUID.randomUUID().toString();
        String enquiry1CouchRev = UUID.randomUUID().toString();
        enquiry1.put("_id", enquiry1CouchId);
        enquiry1.put("_rev", enquiry1CouchRev);
        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry enquiry2 = new Enquiry("field worker 2", new JSONObject("{age:14,name:Subhas}"));
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
        assertTrue(enquiry.getPotentialMatchingIds().length() == 0);

        enquiry.put("enquirer_name", "New Reporter Name");
        enquiry.setCriteria(new JSONObject("{}"));
        enquiry.setCreatedBy("NEW USER");
        enquiry.setSynced(true);
        enquiry.put(Database.EnquiryTableColumn.potential_matches.getColumnName(), "some potential matches id");
        enquiry.put(Database.EnquiryTableColumn.internal_id.getColumnName(), "new internal id");
        enquiry.put(Database.EnquiryTableColumn.internal_rev.getColumnName(), "new internal revision");
        enquiryRepository.update(enquiry);

        Enquiry retrieved = enquiryRepository.all().get(0);

        assertThat(retrieved.optString("enquirer_name"), is("New Reporter Name"));
        assertThat(retrieved.getCriteria().toString(), is("{}"));
        assertThat(retrieved.getCreatedBy().toString(), is("NEW USER"));
        assertTrue(retrieved.isSynced());
        assertThat(retrieved.getString(Database.EnquiryTableColumn.internal_id.getColumnName()), is("new internal id"));
        assertThat(retrieved.getString(Database.EnquiryTableColumn.potential_matches.getColumnName()), is("some potential matches id"));
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
}
