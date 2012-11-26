package com.rapidftr.service;

import android.content.SharedPreferences;
import android.content.res.Resources;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.http.FluentRequest;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.io.SyncFailedException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
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
    public void shouldMarkChildAsSyncedWhenSyncing() throws IOException, JSONException {
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
        Child child = new Child();
        child.put(Database.ChildTableColumn.internal_id.getColumnName(), "xyz");

        getFakeHttpLayer().addHttpResponseRule("http://whatever/children/xyz", "{}");
        new ChildService(mockContext(), repository, fluentRequest).sync(child);
    }

    @Test
    public void shouldCreateNewChildIfThereIsNoID() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        getFakeHttpLayer().addHttpResponseRule("http://whatever/children", "{ 'test1' : 'value2' }");

        child = new ChildService(mockContext(), repository, fluentRequest).sync(child);
        assertThat(child.getString("test1"), is("value2"));
        verify(repository).update(child);
    }

    @Test
    public void shouldFilterChildRecordsThatMatchesSearchString() throws JSONException {
        List<Child> fullChildrenList = new ArrayList<Child>();
        fullChildrenList.add(new Child("id1", "user1", "{ 'name' : 'child1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }"));
        fullChildrenList.add(new Child("id2", "user2", "{ 'name' : 'child2', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }"));
        fullChildrenList.add(new Child("id3", "user3", "{ 'name' : 'child3', 'test2' :  'child1', 'test3' : [ '1', 2, '3' ] }"));
        fullChildrenList.add(new Child("child1", "user4", "{ 'name' : 'child4', 'test2' :  'test2', 'test3' : [ '1', 2, '3' ] }"));
        when(repository.getAllChildren()).thenReturn(fullChildrenList);
        List<Child> searchResults = new ChildService(mockContext(), repository, fluentRequest).searchChildrenInDB("Hild1");
        assertEquals(2,searchResults.size());
        assertEquals("id1", searchResults.get(0).getUniqueId());
        assertEquals("child1", searchResults.get(1).getUniqueId());
    }

    @Test(expected = SyncFailedException.class)
    public void shouldAddPhotoParamIfPhotoIsCapturedAsPartOfChild() throws JSONException, IOException {
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

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = mock(RapidFtrApplication.class);
        Resources resources = mock(Resources.class);
        given(context.getApplicationContext()).willReturn(context);
        SharedPreferences preferences = mock(SharedPreferences.class);
        given(preferences.getString("SERVER_URL", "")).willReturn("whatever");
        given(context.getSharedPreferences(RapidFtrApplication.SHARED_PREFERENCES_FILE, 0)).willReturn(preferences);
        given(context.getResources()).willReturn(resources);
        return context;
    }

}
