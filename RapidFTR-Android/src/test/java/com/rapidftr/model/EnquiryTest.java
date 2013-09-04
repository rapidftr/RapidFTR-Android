package com.rapidftr.model;

import org.json.JSONException;
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
    public void enquiryShouldHaveChildDetails() throws JSONException {
        String enquiryDetails = "{" +
                "\"enquirer_details\":\"Sanchari\"," +
                "\"child_details\" : {\"name\":\"Subhas\", \"age\":14}" +
                "}";
        Enquiry enquiry = new Enquiry("xyz", "kavitha", enquiryDetails);
        assertEquals("{\"age\":14,\"name\":\"Subhas\"}", enquiry.getString("child_details"));
    }
}
