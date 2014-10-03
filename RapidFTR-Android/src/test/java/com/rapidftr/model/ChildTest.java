package com.rapidftr.model;


import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.Database;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.rapidftr.database.Database.ChildTableColumn.internal_id;
import static com.rapidftr.model.History.*;
import static com.rapidftr.utils.JSONMatcher.equalJSONIgnoreOrder;
import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class ChildTest {

    private EnquiryRepository enquiryRepository;
    private PotentialMatchRepository potentialMatchRepository;
    private String user;
    private DatabaseSession session;

    @Before
    public void setUp() throws JSONException {
        user = "Foo";
        session = new ShadowSQLiteHelper("test_database").getSession();
        potentialMatchRepository = new PotentialMatchRepository(user, session);
        enquiryRepository = new EnquiryRepository(user, session);
    }

    @Test
    public void shouldCreateChildWithBlankContent() throws JSONException {
        new Child("        ");
    }

    @Test
    public void shouldDecodeIDFromJSON() throws JSONException {
        Child child = new Child("{ 'unique_identifier' : 'test1' }");
        assertThat(child.getUniqueId(), is("test1"));
    }

    @Test
    public void shouldDecodeOwnerFromJSON() throws JSONException {
        Child child = new Child("{ 'created_by' : 'test1' }");
        assertThat(child.getCreatedBy(), is("test1"));
    }

    @Test
    public void shouldDecodeString() throws JSONException {
        Child child = new Child("{ 'test1' :  'value1' }");
        assertThat(child.getString("test1"), is("value1"));
    }

    @Test
    public void shouldDecodeInteger() throws JSONException {
        Child child = new Child("{ 'test1' : 17 }");
        assertThat(child.getInt("test1"), is(17));
    }

    @Test
    public void shouldDecodeArrayOfStrings() throws JSONException {
        Child child = new Child("{ 'test1' : ['value1', 'value2', 'value3' ]}");
        assertThat(child.getJSONArray("test1").toString(), is(new JSONArray(Arrays.asList("value1", "value2", "value3")).toString()));
    }

    @Test
    public void shouldGenerateWithIdAndOwnerAndContent() throws JSONException {
        Child child = new Child("id1", "owner1", "{ 'test1' : 'value1' }");
        assertThat(child.getUniqueId(), is("id1"));
        assertThat(child.getCreatedBy(), is("owner1"));
        assertThat(child.getString("test1"), is("value1"));
    }

    @Test
    public void shouldNotOverwriteCreatedAtIfGiven() throws JSONException {
        Child child = new Child(String.format("{ 'created_at' :  '%s' }", new RapidFtrDateTime(10, 2, 2012).defaultFormat()));
        assertThat(child.getCreatedAt(), is("2012-02-10 00:00:00"));
    }

    @Test
    public void shouldGenerateUniqueId() throws JSONException {
        Child child = new Child(null, "rapidftr", null);
        child = spy(child);

        doReturn("xyz").when(child).createUniqueId();

        child.generateUniqueId();
        assertThat(child.getUniqueId(), equalTo("xyz"));
    }

    @Test
    public void shouldNotOverwriteIdIfAlreadyPresent() throws JSONException {
        Child child = new Child("id1", "owner1", null);
        child.generateUniqueId();
        assertThat(child.getUniqueId(), equalTo("id1"));
    }

    @Test
    public void shouldReturnShortID() throws JSONException {
        Child child = new Child();
        child.setUniqueId("987654321");

        assertThat(child.getShortId(), equalTo("7654321"));
    }

    @Test
    public void testAtLeastOneFieldIsFilledExcludingIdAndOwner() throws JSONException {
        Child child = new Child("id1", "owner1", null);
        assertThat(child.isValid(), is(false));

        child.put("test1", "value1");
        assertThat(child.isValid(), is(true));

        child.remove("test1");
        assertThat(child.isValid(), is(false));
    }

    @Test
    public void shouldRemoveFromJSONArray() throws JSONException {
        Child child = new Child("{ 'test1' : ['value1', 'value2', 'value3' ]}");
        child.removeFromJSONArray("test1", "value1");
        assertThat(child.getJSONArray("test1").toString(), is(new JSONArray(Arrays.asList("value2", "value3")).toString()));
    }

    @Test
    public void shouldReturnNamesWithLengthOneInsteadOfNull() throws JSONException {
        Child child = new Child();
        assertThat(child.names().length(), equalTo(2)); // synced and created_at
    }

    @Test
    public void shouldRemoveFieldIfBlank() throws JSONException {
        Child child = new Child();
        child.put("name", "test");
        assertThat(child.values().names().length(), equalTo(1));

        child.put("name", "\r  \n  \r  \n");
        assertNull(child.values().names());
    }

    @Test
    public void shouldHaveFalseSyncStatusIfTheChildObjectIsCreated() throws JSONException {
        Child child = new Child();
        assertFalse(child.isSynced());
    }

    @Test
    public void shouldTrimFieldValues() throws JSONException {
        Child child = new Child();
        child.put("name", "\r\n line1 \r\n line2 \r\n \r\n");
        assertThat(child.getString("name"), equalTo("line1 \r\n line2"));
    }

    @Test
    public void valuesShouldReturnAllExceptSystemFields() throws JSONException, IOException {
        Child child = new Child();
        child.put("test1", "value1");
        for (Database.ChildTableColumn column : Database.ChildTableColumn.systemFields()) {
            child.put(column.getColumnName(), "test");
        }

        assertThat(child.values(), equalJSONIgnoreOrder("{\"test1\":\"value1\"}"));
    }

    @Test
    public void shouldBeNewIfThereIsNoID() throws JSONException {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        assertThat(child.isNew(), is(true));
    }

    @Test
    public void shouldNotBeNewIfThereIsID() throws JSONException {
        Child child = new Child();
        child.put(internal_id.getColumnName(), "xyz");
        assertThat(child.isNew(), is(false));
    }

    @Test
    public void testAtLeastOneFieldIsFilledExcludingOwner() throws JSONException {
        Child child = new Child("id1", "owner1", "");
        Assert.assertThat(child.isValid(), is(false));

        child.put("test1", "value1");
        Assert.assertThat(child.isValid(), is(true));

        child.remove("test1");
        Assert.assertThat(child.isValid(), is(false));
    }

    @Test
    public void shouldGetPotentialMatches() throws JSONException, SQLException {
        Child child = new Child("id_1", "", "{'_id' : 'child_id_1'}");

        String enquiryJSON = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_1\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepository.createOrUpdate(enquiry);

        String enquiryJSON2 = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_2\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        enquiryRepository.createOrUpdate(new Enquiry(enquiryJSON2));

        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_1", "child_id_1", "potential_match_id_1"));
        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_2", "child_id_2", "potential_match_id_2"));

        List<BaseModel> enquiries = child.getPotentialMatchingModels(potentialMatchRepository, null, enquiryRepository);

        assertThat(enquiries.size(), is(1));
        assertEquals(enquiry.getUniqueId(), enquiries.get(0).getUniqueId());
    }

    @Test
    public void shouldNotReturnConfirmedMatchesForPotentialMatches() throws JSONException, SQLException {
        Child child = new Child("id_1", "", "{'_id' : 'child_id_1'}");

        String enquiryJSON = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_1\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry = new Enquiry(enquiryJSON);
        enquiryRepository.createOrUpdate(enquiry);

        String enquiryJSON2 = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_2\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        enquiryRepository.createOrUpdate(new Enquiry(enquiryJSON2));

        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_1", "child_id_1", "potential_match_id_1"));
        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_2", "child_id_1", "potential_match_id_2", true));

        List<BaseModel> enquiries = child.getPotentialMatchingModels(potentialMatchRepository, null, enquiryRepository);

        assertThat(enquiries.size(), is(1));
        assertEquals(enquiry.getUniqueId(), enquiries.get(0).getUniqueId());
    }

    @Test
    public void shouldGetConfirmedMatches() throws JSONException, SQLException {
        Child child = new Child("id_1", "", "{'_id' : 'child_id_1'}");

        String enquiryJSON = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_1\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        enquiryRepository.createOrUpdate(new Enquiry(enquiryJSON));

        String enquiryJSON2 = "{\n" +
                "\"synced\":\"true\",\n" +
                "\"_id\":\"enquiry_id_2\",\n" +
                "\"created_by\":\"some guy\"" +
                "}";
        Enquiry enquiry = new Enquiry(enquiryJSON2);
        enquiryRepository.createOrUpdate(enquiry);

        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_1", "child_id_1", "potential_match_id_1"));
        potentialMatchRepository.createOrUpdate(new PotentialMatch("enquiry_id_2", "child_id_1", "potential_match_id_2", true));

        List<BaseModel> enquiries = child.getConfirmedMatchingModels(potentialMatchRepository, null, enquiryRepository);

        assertEquals(1, enquiries.size());
        assertEquals(enquiry.getUniqueId(), enquiries.get(0).getUniqueId());
    }
}
