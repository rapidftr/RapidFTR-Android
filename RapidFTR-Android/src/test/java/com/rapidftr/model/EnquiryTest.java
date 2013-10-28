package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import lombok.Cleanup;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static com.rapidftr.database.Database.EnquiryTableColumn;
import static com.rapidftr.database.Database.EnquiryTableColumn.criteria;
import static com.rapidftr.database.Database.EnquiryTableColumn.potential_matches;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class EnquiryTest {


    private String createdBy;
    private String enquirerName;
    private String enquiryCriteria;
    private DatabaseSession session;
    private ChildRepository childRepository;
    private EnquiryRepository enquiryRepo;
    private String user;

    @Before
    public void setUp() throws JSONException {
        user = "Foo";
        createdBy = "Rajni";
        enquirerName = "Batman";
        enquiryCriteria = "{\"name\":\"NAME\"}";
        session = new ShadowSQLiteHelper("test_database").getSession();
        childRepository = new ChildRepository("user1", session);
        enquiryRepo = new EnquiryRepository(user, session);
    }

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getUniqueId());

        enquiry = new Enquiry("createdBy", "enquirerName", new JSONObject("{}"));
        assertNotNull(enquiry.getUniqueId());
    }


    @Test
    public void createEnquiryWithAllFields() throws JSONException {

        JSONObject criteria = new JSONObject(enquiryCriteria);
        Enquiry enquiry = new Enquiry(createdBy, enquirerName, criteria);

        assertEquals(enquirerName, enquiry.getEnquirerName());
        JSONAssert.assertEquals(criteria, enquiry.getCriteria(), true);
        assertEquals(createdBy, enquiry.getCreatedBy());
        assertNotNull(enquiry.getCreatedAt());
        assertNotNull(enquiry.getLastUpdatedAt());
    }

    @Test
    public void createEnquiryFromCursorShouldPopulateEnquiryUsingAllColumns() throws Exception {
        Cursor cursor = mockedCursor();

        Enquiry enquiry = new Enquiry(cursor);

        assertThat(enquiry.getUniqueId(), is("unique_identifier_value"));
        assertThat(enquiry.getEnquirerName(), is("enquirer_name_value"));
        assertThat(enquiry.getCreatedBy(), is("created_by_value"));
    }

    @Test
    public void shouldPopulateCriteria() throws Exception {
        String enquiryJSON = "{\"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\", \"sex\": \"Male\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);

        JSONObject expectedCriteria = enquiry.getCriteria();

        JSONAssert.assertEquals(enquiryJSON, expectedCriteria, true);
    }
//
//    @Test
//    public void shouldKnowHowToRemoveEnquirerName() throws Exception {
//        String enquiryJSON = "{\"enquirer_name\": \"godwin\", \"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\"}";
//        Enquiry enquiry = new Enquiry(enquiryJSON);
//        String expectedJSON = "{\"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\"}";
//
//        String criteriaJSON = enquiry.getCriteria();
//
//
//        JSONAssert.assertEquals(expectedJSON, criteriaJSON, true);
//    }

    @Test
    public void enquiryShouldGetPotentialMatches() throws JSONException {
        Cursor cursor = mock(Cursor.class);
        doReturn(potential_matches.ordinal()).when(cursor).getColumnIndex(potential_matches.getColumnName());
        doReturn("[\"id1\", \"id2\"]").when(cursor).getString(potential_matches.ordinal());

        Child child1 = new Child("id1", "owner1", "{ 'test1' : 'value1' }");
        childRepository.createOrUpdate(child1);
        Child child2 = new Child("id2", "owner1", "{ 'test1' : 'value1' }");
        childRepository.createOrUpdate(child2);

        Enquiry enquiry = new Enquiry(cursor);
        List<Child> potentialMatches = enquiry.getPotentialMatches(childRepository);

        assertEquals(2, potentialMatches.size());
        assertTrue(potentialMatches.contains(child1));
        assertTrue(potentialMatches.contains(child2));
    }

    @Test
    public void shouldSaveCriteriaWithoutModifyingIt() throws JSONException {
        String enquiryJSON = "{ \"enquirer_name\":\"sam fisher\", \"name\":\"foo bar\"," +
                "\"nationality\":\"ugandan\",\"created_by\" : \"Tom Reed\",\"synced\" : \"false\"}";

        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepo.createOrUpdate(enquiry);

        String expectedCriteria = "{\"name\":\"foo bar\",\"nationality\":\"ugandan\"}";

        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry WHERE enquirer_name = ?", new String[]{"sam fisher"});
        cursor.moveToFirst();

        String actualCriteria = cursor.getString(cursor.getColumnIndex(criteria.getColumnName()));
        JSONAssert.assertEquals(expectedCriteria, actualCriteria, true);
    }

    @Test
    public void criteriaShouldBeAJSONObjectWhenCreatingEnquiryFromCursor() throws JSONException {
        String enquiryJSON = "{\"enquirer_name\":\"sam fisher\",\"name\":\"foo bar\",\"nationality\":\"ugandan\"," +
                "\"created_by\":\"Tom Reed\",\"synced\":\"false\", \"created_organisation\":\"TW\"}";

        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepo.createOrUpdate(enquiry);

        JSONObject expectedCriteria = new JSONObject("{\"name\":\"foo bar\",\"nationality\":\"ugandan\"}");

        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM enquiry WHERE enquirer_name = ?", new String[]{"sam fisher"});
        cursor.moveToFirst();

        Enquiry enquiryFromCursor = new Enquiry(cursor);

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

        assertEquals(expectedEnquirerName, enquiry.getEnquirerName());
        JSONAssert.assertEquals(expectedCriteria, enquiry.getCriteria(), true);
    }

    @Test
    public void enquiryShouldGetMatchingIds() throws JSONException {
        Cursor cursor = mockedCursor();
        Enquiry enquiry = new Enquiry(cursor);
        assertEquals("potential_matches_value", enquiry.matchingChildIds());
    }

    @Test(expected = JSONException.class)
    public void newEnquiryShouldNotHaveMatchingIds() throws JSONException {
        Enquiry enquiry = new Enquiry(createdBy, enquirerName, new JSONObject(enquiryCriteria));
        enquiry.matchingChildIds();
    }

    private Cursor mockedCursor() {
        Cursor cursor = mock(Cursor.class);
        for (EnquiryTableColumn column : EnquiryTableColumn.values()) {
            when(cursor.getColumnIndex(column.getColumnName())).thenReturn(column.ordinal());
            when(cursor.getString(column.ordinal())).thenReturn(column.getColumnName() + "_value");
        }
        return cursor;
    }
}
