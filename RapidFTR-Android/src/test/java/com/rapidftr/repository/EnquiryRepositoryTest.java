package com.rapidftr.repository;

import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void shouldCreateAnEnquiryInTheDatabase() throws JSONException {
        Enquiry enquiry = new Enquiry(user, "REPORTER NAME", new JSONObject("{\"age\":14,\"name\":\"Subhas\"}"));
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
    public void shouldReturnAllEnquiries() throws JSONException{
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry1);
        Enquiry enquiry2 = new Enquiry("field worker 2", "REPORTER NAME 1", new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry2);

        List<Enquiry> enquiries = enquiryRepository.all();
        assertEquals(2, enquiryRepository.size());
        compareEnquiries(enquiry1, enquiries.get(0));
        compareEnquiries(enquiry2, enquiries.get(1));
    }

    private void compareEnquiries(Enquiry enquiry1, Enquiry enquiry2) throws JSONException {
        assertThat(enquiry1.getUniqueId(), is(enquiry2.getUniqueId()));
        assertThat(enquiry1.getOwner(), is(enquiry2.getOwner()));
        assertThat(enquiry1.getEnquirerName(), is(enquiry2.getEnquirerName()));
        assertThat(enquiry1.getCreatedAt(), is(enquiry2.getCreatedAt()));
    }

    @Ignore // get(i) Doesn't need to be implemented for sync all story
    @Test
    public void get_shouldReturnEnquiryForId() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        String enquiryId = enquiry1.getUniqueId();

        enquiryRepository.createOrUpdate(enquiry1);

        assertEquals(enquiryRepository.get(enquiryId).toString(), enquiry1.toString());
    }

    @Test
    public void toBeSynced_shouldReturnAListOfEnquiriesWithSyncedStatusFalse() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        enquiry1.setSynced(false);
        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry enquiry2 = new Enquiry("field worker 2", "REPORTER NAME 1", new JSONObject("{age:14,name:Subhas}"));
        enquiry2.setSynced(true);
        enquiryRepository.createOrUpdate(enquiry2);

        final List<Enquiry> enquiries = enquiryRepository.toBeSynced();

        assertThat(enquiries.size(), is(1));
        assertThat(enquiries.get(0).getUniqueId(), is(enquiry1.getUniqueId()));
    }

    @Test
    public void exists_shouldReturnTrueGivenAnIdOfAnEnquiryThatAlreadyExists() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry1);

        assertTrue(enquiryRepository.exists(enquiry1.getUniqueId()));
        assertFalse(enquiryRepository.exists("100"));
    }
    
    @Test
    public void getAllIdsAndRevs_shouldReturnIdsAndRevsForAllEnquiries() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        String enquiry1CouchId = UUID.randomUUID().toString();
        String enquiry1CouchRev = UUID.randomUUID().toString();
        enquiry1.put("_id", enquiry1CouchId);
        enquiry1.put("_rev", enquiry1CouchRev);
        enquiryRepository.createOrUpdate(enquiry1);

        Enquiry enquiry2 = new Enquiry("field worker 2", "REPORTER NAME 1", new JSONObject("{age:14,name:Subhas}"));
        String enquiry2CouchId = UUID.randomUUID().toString();
        String enquiry2CouchRev = UUID.randomUUID().toString();
        enquiry2.put("_id", enquiry2CouchId);
        enquiry2.put("_rev", enquiry2CouchRev);
        enquiryRepository.createOrUpdate(enquiry2);

        final HashMap<String,String> allIdsAndRevs = enquiryRepository.getAllIdsAndRevs();
        
        assertThat(allIdsAndRevs.size(), is(2));
        assertThat(allIdsAndRevs.get(enquiry1CouchId), is(enquiry1CouchRev));
        assertThat(allIdsAndRevs.get(enquiry2CouchId), is(enquiry2CouchRev));
    }

    @Ignore // Not going to work until createOrUpdate and Enquiry constructor are fixed
    @Test
    public void update_shouldUpdateTheFieldsOfAnEnquiry() throws Exception {
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry1);

        assertThat(enquiryRepository.all().size(), is(1));

        enquiry1.setEnquirerName("New Reporter Name");
        enquiryRepository.update(enquiry1);

        final List<Enquiry> enquiries = enquiryRepository.all();
        assertThat(enquiries.get(0).getEnquirerName(), is("New Reporter Name"));
    }

    


}
