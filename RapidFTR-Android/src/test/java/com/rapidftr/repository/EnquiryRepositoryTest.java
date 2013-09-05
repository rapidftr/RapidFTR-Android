package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.model.Enquiry;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(CustomTestRunner.class)
public class EnquiryRepositoryTest {

    private DatabaseSession session;

    @Before
    public void setUp() {
        session = new ShadowSQLiteHelper("test_database").getSession();
    }

    @Test
    public void shouldCreateAnEnquiryInTheDatabase() throws JSONException {
        EnquiryRepository enquiryRepository = new EnquiryRepository("user", session);
        enquiryRepository.createOrUpdate(new Enquiry("id", "user", "{\"age\":14,\"name\":\"Subhas\"}"));
        assertEquals(1, enquiryRepository.size());
    }
}
