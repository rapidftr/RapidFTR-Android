package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.FailedToSaveException;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static com.rapidftr.database.Database.EnquiryTableColumn.criteria;
import static com.rapidftr.database.Database.EnquiryTableColumn.potential_matches;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(CustomTestRunner.class)
public class EnquiryTest {

    private DatabaseSession session;
    private ChildRepository childRepository;
    private EnquiryRepository enquiryRepo;
    private String user;

    @Before
    public void setUp() throws JSONException {
        user = "Foo";
        session = new ShadowSQLiteHelper("test_database").getSession();
        childRepository = new ChildRepository("user1", session);
        enquiryRepo = new EnquiryRepository(user, session);
    }

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getUniqueId());

        enquiry = new Enquiry("createdBy", new JSONObject("{}"));
        assertNotNull(enquiry.getUniqueId());
    }

    @Test
    public void shouldPopulateCriteria() throws Exception {
        String enquiryJSON = "{\"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\", \"sex\": \"Male\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);

        JSONObject expectedCriteria = enquiry.getCriteria();

        JSONAssert.assertEquals(enquiryJSON, expectedCriteria, true);
    }

    @Test
    public void shouldKnowHowToRemoveEnquirerName() throws Exception {
        String enquiryJSON = "{\"enquirer_name\": \"godwin\", \"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        String expectedJSON = "{\"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\"}";

        JSONObject criteriaJSON = enquiry.getCriteria();

        JSONAssert.assertEquals(expectedJSON, criteriaJSON, true);
    }

    @Test
    public void enquiryShouldGetPotentialMatches() throws JSONException {
        Cursor cursor = mock(Cursor.class);
        doReturn(potential_matches.ordinal()).when(cursor).getColumnIndex(potential_matches.getColumnName());
        doReturn(criteria.ordinal()).when(cursor).getColumnIndex(criteria.getColumnName());
        doReturn("{}").when(cursor).getString(criteria.ordinal());
        doReturn("[\"internal_id1\", \"internal_id2\"]").when(cursor).getString(potential_matches.ordinal());

        Child child1 = new Child("id1", "owner1", "{'test1':'value1', '_id':'internal_id1' }");
        childRepository.createOrUpdate(child1);
        Child child2 = new Child("id2", "owner1", "{'test1':'value1', '_id':'internal_id2' }");
        childRepository.createOrUpdate(child2);

        Enquiry enquiry = new Enquiry(cursor);
        List<Child> potentialMatches = enquiry.getPotentialMatches(childRepository);

        assertEquals(2, potentialMatches.size());
        assertTrue(potentialMatches.contains(child1));
        assertTrue(potentialMatches.contains(child2));
    }

    @Test
    public void criteriaShouldBeAJSONObjectWhenCreatingEnquiryFromCursor() throws JSONException, FailedToSaveException {
        String enquiryJSON = "{\"enquirer_name\":\"sam fisher\",\"name\":\"foo bar\",\"nationality\":\"ugandan\"," +
                "\"created_by\":\"Tom Reed\",\"synced\":\"false\", \"created_organisation\":\"TW\"}";

        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepo.createOrUpdate(enquiry);
        JSONObject expectedCriteria = new JSONObject("{\"name\":\"foo bar\",\"nationality\":\"ugandan\"}");

        Enquiry enquiryFromCursor = enquiryRepo.all().get(0);

        assertEquals(JSONObject.class, enquiryFromCursor.getCriteria().getClass());
        assertEquals(enquiryFromCursor.getCriteria().getString("name"), "foo bar");
        assertEquals(enquiryFromCursor.getCriteria().getString("nationality"), "ugandan");
        JSONAssert.assertEquals(enquiryFromCursor.getCriteria(), expectedCriteria, true);

    }

    @Test
    public void shouldCreateWellFormedEnquiryFromJSONString() throws JSONException {
        String enquiryJSON = "{\"enquirer_name\":\"sam fisher\", \"name\":\"foo bar\", \"nationality\":\"ugandan\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);

        String expectedCriteria = "{\"name\":\"foo bar\", \"nationality\":\"ugandan\"}";
        String expectedEnquirerName = "sam fisher";

        assertEquals(expectedEnquirerName, enquiry.get("enquirer_name"));
        JSONAssert.assertEquals(expectedCriteria, enquiry.getCriteria(), true);
    }

    @Test
    public void enquiryShouldGetMatchingIds() throws JSONException {
        Cursor cursor = mock(Cursor.class);
        doReturn(potential_matches.ordinal()).when(cursor).getColumnIndex(potential_matches.getColumnName());
        doReturn(criteria.ordinal()).when(cursor).getColumnIndex(criteria.getColumnName());
        doReturn("{}").when(cursor).getString(criteria.ordinal());
        doReturn("[\"id1\", \"id2\"]").when(cursor).getString(potential_matches.ordinal());

        Enquiry enquiry = new Enquiry(cursor);

        assertEquals("[\"id1\", \"id2\"]", enquiry.getPotentialMatchingIds());
    }

    @Test
    public void newEnquiryShouldNotHaveMatchingIds() throws JSONException {
        String enquiryJSON = "{\"enquirer_name\":\"sam fisher\", \"name\":\"foo bar\", \"nationality\":\"ugandan\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        assertTrue(enquiry.getPotentialMatchingIds().length() == 0);
    }

    @Test
    public void shouldBeValidEnquiry() throws JSONException {
        String enquiryJSON = "{\"enquirer_name\":\"sam fisher\", \"name\":\"foo bar\", \"nationality\":\"ugandan\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);

        Assert.assertTrue(enquiry.isValid());
    }

    @Test
    public void shouldNotBeValidEnquiry() throws JSONException {
        String enquiryJSON = "{}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        Assert.assertFalse(enquiry.isValid());
    }
}
