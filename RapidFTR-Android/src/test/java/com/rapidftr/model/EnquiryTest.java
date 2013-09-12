package com.rapidftr.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class EnquiryTest {

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getUniqueId());
    }


    @Test
    public void createEnquiryWithAllFields() throws JSONException{
      String createdBy = "Rajni";
      String reporterName = "Batman";
      JSONObject reporterDetails = new JSONObject("{\"sex\": \"Male\"}");
      JSONObject criteria = new JSONObject("{\"name\":\"NAME\"}");
      Enquiry enquiry = new Enquiry(createdBy, reporterName, reporterDetails, criteria);

      assertEquals(reporterName, enquiry.getReporterName());
      assertEquals(enquiry.getReporterDetails().getClass(), JSONObject.class);
      assertEquals(reporterDetails.toString(), enquiry.getReporterDetails().toString());
      assertEquals(enquiry.getCriteria().getClass(), JSONObject.class);
      assertEquals(criteria.toString(), enquiry.getCriteria().toString());
      assertEquals(createdBy, enquiry.getCreatedBy());
      assertNotNull(enquiry.getCreatedAt());
      assertNotNull(enquiry.getLastUpdatedAt());
    }
}
