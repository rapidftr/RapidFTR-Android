package com.rapidftr.model;

import android.database.Cursor;
import com.rapidftr.database.Database;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnquiryTest {

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getId());
    }


    @Test
    public void createEnquiryWithAllFields() throws JSONException{
      String createdBy = "Rajni";
      String reporterName = "Batman";
      JSONObject criteria = new JSONObject("{\"name\":\"NAME\"}");
      Enquiry enquiry = new Enquiry(createdBy, reporterName, criteria);

      assertEquals(reporterName, enquiry.getEnquirerName());
      assertEquals(enquiry.getCriteria().getClass(), JSONObject.class);
      assertEquals(criteria.toString(), enquiry.getCriteria().toString());
      assertEquals(createdBy, enquiry.getCreatedBy());
      assertNotNull(enquiry.getCreatedAt());
      assertNotNull(enquiry.getLastUpdatedAt());
    }

    @Test
    public void createEnquiryFromCursor_shouldPopulateEnquiryUsingAllColumns() throws Exception {
        Cursor cursor = mock(Cursor.class);
        for(Database.EnquiryTableColumn column : Database.EnquiryTableColumn.values()) {
            when(cursor.getColumnIndex(column.getColumnName())).thenReturn(column.ordinal());
            when(cursor.getString(column.ordinal())).thenReturn(column.getColumnName() + "_value");
        }

        Enquiry enquiry = new Enquiry(cursor);

        assertThat(enquiry.getId(), is("id_value"));
        assertThat(enquiry.getEnquirerName(), is("enquirer_name_value"));
        assertThat(enquiry.getOwner(), is("created_by_value"));
    }


}
