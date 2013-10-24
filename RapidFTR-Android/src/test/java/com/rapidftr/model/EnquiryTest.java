package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.repository.ChildRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static com.rapidftr.database.Database.EnquiryTableColumn;
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
    private JSONObject criteria;
    private DatabaseSession session;
    private ChildRepository childRepository;

    @Before
    public void setUp() throws JSONException {
        createdBy = "Rajni";
        enquirerName = "Batman";
        criteria = new JSONObject("{\"name\":\"NAME\"}");
        session = new ShadowSQLiteHelper("test_database").getSession();
        childRepository = new ChildRepository("user1", session);
    }

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getUniqueId());

        enquiry = new Enquiry("createdBy", "enquirerName", new JSONObject("{}"));
        assertNotNull(enquiry.getUniqueId());
    }


    @Test
    public void createEnquiryWithAllFields() throws JSONException{

      Enquiry enquiry = new Enquiry(createdBy, enquirerName, criteria);

      assertEquals(enquirerName, enquiry.getEnquirerName());
      assertEquals(enquiry.getCriteria().getClass(), JSONObject.class);
      JSONAssert.assertEquals(criteria, enquiry.getCriteria(), true);
      assertEquals(createdBy, enquiry.getCreatedBy());
      assertNotNull(enquiry.getCreatedAt());
      assertNotNull(enquiry.getLastUpdatedAt());
    }

    @Test
    public void createEnquiryFromCursor_shouldPopulateEnquiryUsingAllColumns() throws Exception {
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
        JSONObject expectedJSON = new JSONObject(enquiryJSON);

        JSONObject criteriaJSON = enquiry.getCriteria();

        JSONAssert.assertEquals(expectedJSON, criteriaJSON, true);
    }

    @Test
    public void shouldKnowHowToRemoveEnquirerName() throws Exception {
        String enquiryJSON = "{\"enquirer_name\": \"godwin\", \"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        JSONObject expectedJSON = new JSONObject("{\"name\": \"robin\", \"age\": \"10\", \"location\": \"Kampala\"}");

        JSONObject criteriaJSON = enquiry.getCriteria();

        JSONAssert.assertEquals(expectedJSON, criteriaJSON, true);
    }

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

    //need to rewrite this test
    @Test
    public void enquiryShouldGetMatchingIds() throws JSONException {
        Cursor cursor = mockedCursor();
        Enquiry enquiry = new Enquiry(cursor);
        assertEquals("potential_matches_value", enquiry.matchingChildIds());
    }

    @Test(expected=JSONException.class)
    public void newEnquiryShouldNotHaveMatchingIds() throws JSONException {
        Enquiry enquiry = new Enquiry(createdBy, enquirerName, criteria);
        enquiry.matchingChildIds();
    }

    @Test
    public void createEnquiryFromCursorShouldPopulateEnquiryUsingAllColumns() throws Exception {
        Cursor cursor = mockedCursor();

        Enquiry enquiry = new Enquiry(cursor);

        assertThat(enquiry.getUniqueId(), is("unique_identifier_value"));
        assertThat(enquiry.getEnquirerName(), is("enquirer_name_value"));
        assertThat(enquiry.getCreatedBy(), is("created_by_value"));
    }

    private Cursor mockedCursor(){
        Cursor cursor = mock(Cursor.class);
        for(EnquiryTableColumn column : EnquiryTableColumn.values()) {
            when(cursor.getColumnIndex(column.getColumnName())).thenReturn(column.ordinal());
            when(cursor.getString(column.ordinal())).thenReturn(column.getColumnName() + "_value");
        }
        return cursor;
    }
}
