package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
    }

    @Test
    public void shouldReturnAllEnquiries() throws JSONException{
        Enquiry enquiry1 = new Enquiry(user, "REPORTER NAME", new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry1);
        Enquiry enquiry2 = new Enquiry("field worker 2", "REPORTER NAME 1", new JSONObject("{age:14,name:Subhas}"));
        enquiryRepository.createOrUpdate(enquiry2);

        List<Enquiry> enquiries = enquiryRepository.all();
        assertEquals(2, enquiryRepository.size());
        assertEquals(enquiry1.getClass(), enquiries.get(0).getClass());
        assertEquals(enquiry1.toString(), enquiries.get(0).toString());
        assertEquals(enquiry2.getClass(), enquiries.get(1).getClass());
        assertEquals(enquiry2.toString(), enquiries.get(1).toString());
    }
}
