package com.rapidftr.model;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(CustomTestRunner.class)
public class EnquiryTest {

    private DatabaseSession session;
    private ChildRepository childRepository;
    private PotentialMatchRepository potentialMatchRepository;
    private EnquiryRepository enquiryRepo;
    private String user;

    @Before
    public void setUp() throws JSONException {
        user = "Foo";
        session = new ShadowSQLiteHelper("test_database").getSession();
        childRepository = new ChildRepository("user1", session);
        potentialMatchRepository = new PotentialMatchRepository("user1", session);
        enquiryRepo = new EnquiryRepository(user, session);
    }

    @Test
    public void shouldAutoGenerateAUniqueID() throws JSONException {
        Enquiry enquiry = new Enquiry();
        assertNotNull(enquiry.getUniqueId());

        enquiry = new Enquiry("{}", "createdBy");
        assertNotNull(enquiry.getUniqueId());
    }

    @Test
    public void enquiryShouldGetPotentialMatches() throws JSONException, SQLException {
        Child child1 = new Child("id1", "owner1", "{'test':'a','_id':'child_id_1' }");
        Child child2 = new Child("id2", "owner1", "{'test':'a','_id':'child_id_2' }");
        Child child3 = new Child("id3", "owner1", "{'test':'a','_id':'child_id_3' }");
        childRepository.createOrUpdate(child1);
        childRepository.createOrUpdate(child2);
        childRepository.createOrUpdate(child3);
        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_1", "child_id_1", "potential_match_id_1"));
        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_1", "child_id_3", "potential_match_id_2"));

        String enquiryJSON = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_1\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        List<BaseModel> children = enquiry.getPotentialMatchingModels(potentialMatchRepository, childRepository, null);

        assertEquals(2, children.size());
        assertTrue(children.contains(child1));
        assertTrue(children.contains(child3));
    }

    @Test
    public void shouldCreateWellFormedEnquiryFromJSONString() throws JSONException {
        String enquiryJSON = "{\"enquirer_name\":\"sam fisher\", \"name\":\"foo bar\", \"nationality\":\"ugandan\"}";
        Enquiry enquiry = new Enquiry(enquiryJSON);

        String expectedEnquirerName = "sam fisher";
        String expectedNationality = "ugandan";

        assertEquals(expectedEnquirerName, enquiry.get("enquirer_name"));
        assertEquals(expectedNationality, enquiry.get("nationality"));
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
