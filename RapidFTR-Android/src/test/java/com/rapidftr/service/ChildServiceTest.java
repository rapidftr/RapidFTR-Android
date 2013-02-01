package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.http.FluentRequest;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.io.SyncFailedException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ChildServiceTest {

    @Mock
    ChildRepository repository;

    FluentRequest fluentRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        fluentRequest = new FluentRequest();
    }

    @Test
    public void shouldParseJsonResponseAndConvertToChildren() throws IOException, JSONException {

        String response = "{\"photo_keys\":[],\"evacuation_status\":\"\",\"id_document\":\"\",\"disclosure_authorities\":\"\",\"wishes_address_1\":\"\",\"ethnicity_or_tribe\":\"\",\"evacuation_date\":\"\",\"other_child_1_telephone\":\"\",\"concerns_followup_details\":\"\",\"nick_name\":\"\",\"rc_id_no\":\"\",\"wishes_name_3\":\"\",\"disclosure_deny_details\":\"\",\"concerns_needs_followup\":\"\",\"interview_place\":\"\",\"care_arrangements\":\"\",\"concerns_further_info\":\"\",\"fathers_name\":\"\",\"wishes_wants_contact\":\"\",\"governing_org\":\"\",\"disclosure_public_name\":\"\",\"other_child_2_telephone\":\"\",\"separation_details\":\"\",\"care_arrangements_address\":\"\",\"other_child_3\":\"\",\"caregivers_name\":\"\",\"separation_place\":\"\",\"mothers_name\":\"\",\"concerns_other\":\"\",\"wishes_telephone_2\":\"\",\"is_caregiver_alive\":\"\",\"disclosure_public_photo\":\"\",\"care_arrangements_came_from\":\"\",\"other_child_3_dob\":\"\",\"wishes_address_2\":\"\",\"couchrest-type\":\"Child\",\"_id\":\"be5d92ff528c8d82c8753934a7712eb1\",\"additional_tracing_info\":\"\",\"care_arrangments_name\":\"\",\"concerns_girl_mother\":\"\",\"_rev\":\"1-dfd36730cc723481387511804b21a320\",\"mother_death_details\":\"\",\"interview_subject\":\"\",\"other_org_place\":\"\",\"other_org_name\":\"\",\"care_arrangements_relationship\":\"\",\"wishes_name_1\":\"\",\"concerns_medical_case\":\"\",\"other_child_3_telephone\":\"\",\"disclosure_public_relatives\":\"\",\"concerns_chh\":\"\",\"wishes_contacted\":\"\",\"name\":\"Akash Bhalla\",\"unique_identifier\":\"fworkerxxxdea2f\",\"evacuation_from\":\"\",\"interview_subject_details\":\"\",\"father_death_details\":\"\",\"interview_date\":\"\",\"characteristics\":\"\",\"documents\":\"\",\"other_child_2_relationship\":\"\",\"evacuation_to\":\"\",\"created_by_full_name\":\"testing\",\"is_father_alive\":\"\",\"other_child_1_address\":\"\",\"names_origin\":\"\",\"wishes_telephone_3\":\"\",\"other_child_1\":\"\",\"birthplace\":\"\",\"care_arrangements_arrival_date\":\"\",\"other_child_1_relationship\":\"\",\"other_child_3_birthplace\":\"\",\"concerns_street_child\":\"\",\"wishes_address_3\":\"\",\"telephone\":\"\",\"other_child_2_birthplace\":\"\",\"other_child_1_birthplace\":\"\",\"current_photo_key\":null,\"protection_status\":\"\",\"care_arrangements_knowsfamily\":\"\",\"care_arrangements_other\":\"\",\"histories\":[],\"interviewers_org\":\"\",\"created_by\":\"fworker\",\"other_child_3_relationship\":\"\",\"concerns_abuse_situation\":\"\",\"address\":\"\",\"wishes_name_2\":\"\",\"other_child_1_dob\":\"\",\"other_org_interview_status\":\"\",\"gender\":\"\",\"posted_from\":\"Browser\",\"nationality\":\"\",\"separation_date\":\"\",\"other_child_2_dob\":\"\",\"evacuation_agent\":\"\",\"wishes_contacted_details\":\"\",\"other_family\":\"\",\"created_at\":\"2012-10-26 09:28:39UTC\",\"languages\":\"\",\"other_child_3_address\":\"\",\"is_mother_alive\":\"\",\"other_org_country\":\"\",\"dob_or_age\":\"\",\"concerns_disabled\":\"\",\"other_org_date\":\"\",\"concerns_vulnerable_person\":\"\",\"care_arrangements_familyinfo\":\"\",\"disclosure_other_orgs\":\"\",\"interviewer\":\"\",\"orther_org_reference_no\":\"\",\"other_child_2\":\"\",\"wishes_telephone_1\":\"\",\"other_child_2_address\":\"\",\"posted_at\":\"2012-10-26 09:28:39UTC\"}";
        getFakeHttpLayer().setDefaultHttpResponse(201, "[" + response + "]");

        List<Child> children = new ChildService(mockContext(), repository, fluentRequest).getAllChildren();
        assertThat(children.size(), is(1));
        assertThat(children.get(0), equalTo(new Child(response)));
    }

    @Test
    public void shouldMarkChildAsSyncedWhenSyncing() throws IOException, JSONException, GeneralSecurityException {
        Child child = new Child();
        getFakeHttpLayer().setDefaultHttpResponse(201, "{}");

        child = new ChildService(mockContext(), repository, fluentRequest).sync(child);
        assertThat(child.isSynced(), is(true));
        verify(repository).update(child);
    }

    @Test(expected = SyncFailedException.class)
    public void shouldRaiseExceptionUponSyncFailure() throws Exception {
        Child child = new Child();
        getFakeHttpLayer().setDefaultHttpResponse(503, "error");

        new ChildService(mockContext(), repository, fluentRequest).sync(child);
    }

    @Test
    public void shouldCallServerURLWithCouchID() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        child.put(Database.ChildTableColumn.internal_id.getColumnName(), "xyz");

        getFakeHttpLayer().addHttpResponseRule("http://whatever/children/xyz", "{}");
        new ChildService(mockContext(), repository, fluentRequest).sync(child);
    }

    @Test
    public void shouldCreateNewChildIfThereIsNoID() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        getFakeHttpLayer().addHttpResponseRule("http://whatever/children", "{ 'test1' : 'value2', '_id' : 'abcd1234'}");

        getFakeHttpLayer().addHttpResponseRule("http://whatever/children/abcd1234/photo/", "{}");
        child = new ChildService(mockContext(), repository, fluentRequest).sync(child);
        assertThat(child.getString("test1"), is("value2"));
        verify(repository).update(child);
    }

    @Test(expected = SyncFailedException.class)
    public void shouldAddPhotoParamIfPhotoIsCapturedAsPartOfChild() throws JSONException, IOException, GeneralSecurityException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        Child child = new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'current_photo_key' : '1234ABC'}");
        RapidFtrApplication context = mockContext();
        doReturn(null).when(mockFluentRequest).postWithMultipart();

        new ChildService(context, repository, mockFluentRequest).sync(child);
        verify(mockFluentRequest).param("current_photo_key", "1234ABC");
    }

    @Test
    public void shouldFetchPrimaryPhotoFromServer() throws JSONException, IOException, GeneralSecurityException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        Child child = new Child("id1", "user1", "{ '_id' : '1234abcd' ,'current_photo_key' : 'image_file_name'}");
        getFakeHttpLayer().setDefaultHttpResponse(200, "image stream");

        new ChildService(mockContext(), repository, mockFluentRequest).getPhoto(child);

        verify(mockFluentRequest).path("/children/1234abcd/photo/image_file_name");
    }

    @Test
    public void shouldFetchAudioFromServer() throws JSONException, IOException, GeneralSecurityException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        Child child = new Child("id1", "user1", "{ '_id' : '1234abcd' ,'recorded_audio' : 'audio_file_name'}");
        getFakeHttpLayer().setDefaultHttpResponse(200, "audio stream");

        new ChildService(mockContext(), repository, mockFluentRequest).getAudio(child);

        verify(mockFluentRequest).path("/children/1234abcd/audio/audio_file_name");
    }

    @Test
    public void shouldFetchAllIdRevs() throws IOException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());

        String response = "[{\"_rev\":\"5-1ed26a0e5072830a9064361a570684f6\",\"_id\":\"dfb2031ebfcbef39dccdb468f5200edc\"},{\"_rev\":\"4-b011946150a16b0d2c6271aed05e2abe\",\"_id\":\"59cd40f39ab6aa791f73885e3bdd99f9\"}]";
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        Map<String,String> allIdRevs = new ChildService(mockContext(), repository, mockFluentRequest).getAllIdsAndRevs();
        assertEquals(2, allIdRevs.size());
        assertEquals("5-1ed26a0e5072830a9064361a570684f6", allIdRevs.get("dfb2031ebfcbef39dccdb468f5200edc"));
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));

        verify(mockFluentRequest).path("/children-ids");
    }

    @Test
    public void shouldGetChild() throws IOException, JSONException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        String response = "{\"separation_place\":\"\",\"wishes_address_3\":\"\",\"care_arrangments_name\":\"\",\"other_family\":\"\",\"care_arrangements_knowsfamily\":\"\",\"created_at\":\"2012-12-14 10:57:39UTC\",\"wishes_contacted_details\":\"\",\"posted_from\":\"Browser\",\"care_arrangements_relationship\":\"\",\"interviewer\":\"\",\"birthplace\":\"\",\"father_death_details\":\"\",\"mothers_name\":\"\",\"name\":\"kavitha working\",\"other_child_2_relationship\":\"\",\"other_child_1\":\"\",\"other_child_1_dob\":\"\",\"other_child_2_telephone\":\"\",\"caregivers_name\":\"\",\"other_child_3_dob\":\"\",\"concerns_medical_case\":\"\",\"names_origin\":\"\",\"gender\":\"\",\"unique_identifier\":\"8a126c33-d2e3-4802-8698-19c06f52d5d1\",\"is_caregiver_alive\":\"\",\"wishes_contacted\":\"\",\"other_child_3_address\":\"\",\"evacuation_from\":\"\",\"photo_keys\":[],\"address\":\"\",\"disclosure_other_orgs\":\"\",\"concerns_other\":\"\",\"histories\":[],\"wishes_wants_contact\":\"\",\"wishes_telephone_1\":\"\",\"posted_at\":\"2012-12-14 10:57:39UTC\",\"other_child_1_address\":\"\",\"other_child_3_birthplace\":\"\",\"other_child_3_relationship\":\"\",\"languages\":\"\",\"concerns_followup_details\":\"\",\"other_org_interview_status\":\"\",\"concerns_further_info\":\"\",\"concerns_needs_followup\":\"\",\"disclosure_public_photo\":\"\",\"wishes_name_1\":\"\",\"created_by\":\"rapidftr\",\"other_org_date\":\"\",\"wishes_address_1\":\"\",\"is_mother_alive\":\"\",\"other_child_1_relationship\":\"\",\"other_child_1_telephone\":\"\",\"interview_place\":\"\",\"evacuation_date\":\"\",\"evacuation_status\":\"\",\"other_child_2\":\"\",\"c206ec4e\":\"\",\"other_child_2_dob\":\"\",\"interviewers_org\":\"\",\"dob_or_age\":\"\",\"id_document\":\"\",\"care_arrangements_arrival_date\":\"\",\"rc_id_no\":\"\",\"care_arrangements_came_from\":\"\",\"protection_status\":\"\",\"other_org_place\":\"\",\"separation_date\":\"\",\"created_organisation\":\"N/A\",\"mother_death_details\":\"\",\"concerns_girl_mother\":\"\",\"e96c289e\":\"\",\"orther_org_reference_no\":\"\",\"_rev\":\"1-ec347c93b262e7db0e306b77f22c2e19\",\"evacuation_to\":\"\",\"disclosure_authorities\":\"\",\"c9fc0344\":\"\",\"wishes_telephone_2\":\"\",\"interview_date\":\"\",\"telephone\":\"\",\"evacuation_agent\":\"\",\"additional_tracing_info\":\"\",\"couchrest-type\":\"Child\",\"care_arrangements\":\"\",\"other_child_2_birthplace\":\"\",\"disclosure_public_relatives\":\"\",\"other_child_2_address\":\"\",\"wishes_name_2\":\"\",\"current_photo_key\":\"\",\"disclosure_public_name\":\"\",\"separation_details\":\"\",\"interview_subject_details\":\"\",\"wishes_address_2\":\"\",\"concerns_abuse_situation\":\"\",\"063c3784\":\"\",\"concerns_street_child\":\"\",\"other_child_3\":\"\",\"interview_subject\":\"\",\"care_arrangements_address\":\"\",\"documents\":\"\",\"other_child_1_birthplace\":\"\",\"fef83a5e\":\"\",\"is_father_alive\":\"\",\"created_by_full_name\":\"RapidFTR\",\"characteristics\":\"\",\"care_arrangements_familyinfo\":\"\",\"disclosure_deny_details\":\"\",\"other_org_name\":\"\",\"nationality\":\"\",\"short_id\":\"f52d5d1\",\"concerns_chh\":\"\",\"concerns_vulnerable_person\":\"\",\"wishes_telephone_3\":\"\",\"concerns_disabled\":\"\",\"fathers_name\":\"\",\"_id\":\"0369c92c8e2245e680dc9a580202e285\",\"other_org_country\":\"\",\"ethnicity_or_tribe\":\"\",\"care_arrangements_other\":\"\",\"wishes_name_3\":\"\"}";
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        Child child = new ChildService(mockContext(), repository, mockFluentRequest).getChild("0369c92c8e2245e680dc9a580202e285");
        assertEquals("kavitha working", child.get("name"));
        assertEquals("1-ec347c93b262e7db0e306b77f22c2e19", child.get("_rev"));

        verify(mockFluentRequest).path("/children/0369c92c8e2245e680dc9a580202e285");
    }

    @Test
    public void shouldSyncUnverifiedChild() throws Exception {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        getFakeHttpLayer().addHttpResponseRule("POST", "http://whatever/children/sync_unverified", new TestHttpResponse(200, "{}"));

        new ChildService(mockContext(), repository, mockFluentRequest).syncUnverified(mock(Child.class));
    }

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }

}
