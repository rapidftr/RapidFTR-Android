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

import java.util.List;

import static com.rapidftr.database.Database.*;
import static com.rapidftr.database.Database.EnquiryTableColumn.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class EnquiryTest {


    private String createdBy;
    private String reporterName;
    private JSONObject search_criteria;
    private DatabaseSession session;
    private ChildRepository childRepository;

    @Before
    public void setUp() throws JSONException {
        createdBy = "Rajni";
        reporterName = "Batman";
        search_criteria = new JSONObject("{\"name\":\"NAME\"}");
        session = new ShadowSQLiteHelper("test_database").getSession();
        childRepository = new ChildRepository("user1", session);
    }

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getUniqueId());

        enquiry = new Enquiry("createdBy", "reporterName", new JSONObject("{}"));
        assertNotNull(enquiry.getUniqueId());
    }


    @Test
    public void createEnquiryWithAllFields() throws JSONException{

      Enquiry enquiry = new Enquiry(createdBy, reporterName, search_criteria);

      assertEquals(reporterName, enquiry.getEnquirerName());
      assertEquals(enquiry.getCriteria().getClass(), JSONObject.class);
      assertEquals(search_criteria.toString(), enquiry.getCriteria().toString());
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
    public void enquiryShouldGetMatchingIds() throws JSONException {
        Cursor cursor = mock(Cursor.class);
        doReturn(potential_matches.ordinal()).when(cursor).getColumnIndex(potential_matches.getColumnName());
        doReturn("[\"id1\", \"id2\"]").when(cursor).getString(potential_matches.ordinal());

        Enquiry enquiry = new Enquiry(cursor);

        assertEquals("[\"id1\", \"id2\"]", enquiry.matchingChildIds());
    }

    @Test(expected=JSONException.class)
    public void newEnquiryShouldNotHaveMatchingIds() throws JSONException {
        Enquiry enquiry = new Enquiry(createdBy, reporterName, search_criteria);
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
