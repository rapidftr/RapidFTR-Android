package com.rapidftr.service;

import android.graphics.Bitmap;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.utils.http.FluentRequest;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.apache.http.HttpException;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomTestRunner.class)
public class ChildServiceTest {

    @Mock private ChildRepository repository;
    @Mock private User currentUser;

    FluentRequest fluentRequest;
    public static final String RESPONSE = "{\"unique_identifier\":\"adf7c0c9-0137-4cae-beea-b7d282344829\",\"created_at\":\"2013-02-08 12:18:37\",\"created_by_full_name\":\"RapidFTR\",\"couchrest-type\":\"Child\",\"short_id\":\"2344829\",\"_id\":\"b7f89b978870da823e0af6491c3e295b\",\"_rev\":\"2-bc72af384e177fcaa8e9e8d181bfe05b\",\"name\":\"\",\"last_updated_at\":\"2013-02-08 11:37:33\",\"current_photo_key\":\"photo--1475374810-2013-02-08T175138\",\"created_by\":\"rapidftr\",\"photo_keys\":[\"photo--1475374810-2013-02-08T175138\"],\"created_organisation\":\"N/A\",\"posted_at\":\"2013-02-08 12:16:55UTC\",\"last_updated_by_full_name\":\"RapidFTR\"}";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        given(currentUser.isVerified()).willReturn(true);
        fluentRequest = new FluentRequest();
    }

    @Test
    public void shouldMarkChildAsSyncedWhenSyncing() throws IOException, JSONException, GeneralSecurityException {
        Child child = new Child();
        getFakeHttpLayer().setDefaultHttpResponse(201, "{}");

        child = new ChildService(mockContext(), repository, fluentRequest).sync(child, currentUser);
        assertThat(child.isSynced(), is(true));
        verify(repository).update(child);
    }

    @Test
    public void shouldHandleSyncFailureAndReturnTheFailingChild() throws Exception {
        Child child = new Child();
        getFakeHttpLayer().setDefaultHttpResponse(503, "error");

        assertEquals(child, new ChildService(mockContext(), repository, fluentRequest).sync(child, currentUser));
    }

    @Test
    public void shouldCallServerURLWithCouchID() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        child.put(Database.ChildTableColumn.internal_id.getColumnName(), "xyz");

        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children/xyz", "{}");
        new ChildService(mockContext(), repository, fluentRequest).sync(child, currentUser);
    }

    @Test
    public void shouldCreateNewChildIfThereIsNoID() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children", "{ 'test1' : 'value2', '_id' : 'abcd1234'}");

        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children/abcd1234/photo/", "{}");
        child = new ChildService(mockContext(), repository, fluentRequest).sync(child, currentUser);

        verify(repository).update(child);
    }

    @Test
    public void shouldAddPhotoKeysToParam() throws JSONException, IOException, GeneralSecurityException {
        getFakeHttpLayer().setDefaultHttpResponse(201, RESPONSE );
        RapidFtrApplication context = mockContext();
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        ChildService childService = spy(new ChildService(context, repository, mockFluentRequest));


        String photoKeys = new JSONArray(Arrays.asList("photo-998877", "photo-998547", "abcd123", "1234ABC")).toString();
        String childDetails = String.format("{ '_id' : 'abcdef', 'name' : 'child1', 'test2' : 0, 'current_photo_key' : '1234ABC', 'photo_keys' : %s}", photoKeys);
        Child child = new Child("id1", "user1", childDetails);

        doNothing().when(mockFluentRequest).addPhotoToMultipart(Matchers.any(MultipartEntity.class), Matchers.any(String.class));
        doNothing().when(childService).savePhoto(Matchers.any(Bitmap.class), Matchers.any(PhotoCaptureHelper.class), Matchers.anyString());
        childService.sync(child, currentUser);
        verify(mockFluentRequest).param("photo_keys", new JSONArray(Arrays.asList("abcd123", "1234ABC")).toString());
    }

    @Test
    public void shouldNotAddPhotoParamIfThePhotoNameIsPresentInPhotoKeys() throws JSONException, IOException, GeneralSecurityException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());

        String photoKeys = new JSONArray(Arrays.asList("photo-998877", "photo-998547", "1234ABC")).toString();
        String childDetails = String.format("{ 'name' : 'child1', 'test2' : 0, 'current_photo_key' : '1234ABC', 'photo_keys' : %s}", photoKeys);


        Child child = new Child("id1", "user1", childDetails);
        RapidFtrApplication context = mockContext();
        doReturn(null).when(mockFluentRequest).postWithMultipart();

        new ChildService(context, repository, mockFluentRequest).sync(child, currentUser);
        verify(mockFluentRequest, times(0)).param("current_photo_key", "1234ABC");
    }

    @Test
    public void shouldAddAudioRecordedIfCreatedOnTheMobile() throws JSONException, IOException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        RapidFtrApplication context = mockContext();

        doReturn(null).when(mockFluentRequest).postWithMultipart();

        Child child = new Child("id","user","{'name' : 'child1', 'recorded_audio' : '123455'}");
        new ChildService(context, repository, mockFluentRequest).sync(child, currentUser);
        verify(mockFluentRequest).param("recorded_audio", "123455");
    }

    @Test
    public void shouldNotAddAudioRecordedToTheRequestIfItsAlreadyPresentInServer() throws JSONException, IOException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        RapidFtrApplication context = mockContext();

        doReturn(null).when(mockFluentRequest).postWithMultipart();

        Child child = new Child("id","user","{'name' : 'child1', 'recorded_audio' : '123455', 'audio_attachments' : {'original' : '123455', 'amr':'123455'}}");
        new ChildService(context, repository, mockFluentRequest).sync(child, currentUser);
        verify(mockFluentRequest, times(0)).param("recorded_audio", "123455");
    }

    @Test
    public void shouldRemoveUnusedParametersBeforeSync() throws JSONException, IOException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        RapidFtrApplication context = mockContext();

        String photoKeys = new JSONArray(Arrays.asList("photo-998877", "photo-998547", "1234ABC")).toString();
        String childDetails = String.format("{ 'name' : 'child1', 'test2' : 0, 'current_photo_key' : '1234ABC', 'recorded_audio' : '123455', 'audio_attachments' : {'original' : '123455', 'amr':'123455'}, 'photo_keys' : %s}", photoKeys);
        doReturn(null).when(mockFluentRequest).postWithMultipart();

        Child child = new Child("id","user",childDetails);
        new ChildService(context, repository, mockFluentRequest).sync(child, currentUser);
        assertThat(child.optString("photo_keys"), is(""));
        assertThat(child.optString("audio_attachments"), is(""));
    }

    @Test
    public void shouldUpdateChildAttributesAfterSync() throws IOException, JSONException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        RapidFtrApplication context = mockContext();

        String response = "{\"separation_place\":\"\",\"wishes_address_3\":\"\",\"care_arrangments_name\":\"\",\"other_family\":\"\",\"care_arrangements_knowsfamily\":\"\",\"created_at\":\"2012-12-14 10:57:39UTC\",\"wishes_contacted_details\":\"\",\"posted_from\":\"Browser\",\"care_arrangements_relationship\":\"\",\"interviewer\":\"\",\"birthplace\":\"\",\"father_death_details\":\"\",\"mothers_name\":\"\",\"name\":\"kavitha working\",\"other_child_2_relationship\":\"\",\"other_child_1\":\"\",\"other_child_1_dob\":\"\",\"other_child_2_telephone\":\"\",\"caregivers_name\":\"\",\"other_child_3_dob\":\"\",\"concerns_medical_case\":\"\",\"names_origin\":\"\",\"gender\":\"\",\"unique_identifier\":\"8a126c33-d2e3-4802-8698-19c06f52d5d1\",\"is_caregiver_alive\":\"\",\"wishes_contacted\":\"\",\"other_child_3_address\":\"\",\"evacuation_from\":\"\",\"photo_keys\":[],\"address\":\"\",\"disclosure_other_orgs\":\"\",\"concerns_other\":\"\",\"histories\":[],\"wishes_wants_contact\":\"\",\"wishes_telephone_1\":\"\",\"posted_at\":\"2012-12-14 10:57:39UTC\",\"other_child_1_address\":\"\",\"other_child_3_birthplace\":\"\",\"other_child_3_relationship\":\"\",\"languages\":\"\",\"concerns_followup_details\":\"\",\"other_org_interview_status\":\"\",\"concerns_further_info\":\"\",\"concerns_needs_followup\":\"\",\"disclosure_public_photo\":\"\",\"wishes_name_1\":\"\",\"created_by\":\"rapidftr\",\"other_org_date\":\"\",\"wishes_address_1\":\"\",\"is_mother_alive\":\"\",\"other_child_1_relationship\":\"\",\"other_child_1_telephone\":\"\",\"interview_place\":\"\",\"evacuation_date\":\"\",\"evacuation_status\":\"\",\"other_child_2\":\"\",\"c206ec4e\":\"\",\"other_child_2_dob\":\"\",\"interviewers_org\":\"\",\"dob_or_age\":\"\",\"id_document\":\"\",\"care_arrangements_arrival_date\":\"\",\"rc_id_no\":\"\",\"care_arrangements_came_from\":\"\",\"protection_status\":\"\",\"other_org_place\":\"\",\"separation_date\":\"\",\"created_organisation\":\"N/A\",\"mother_death_details\":\"\",\"concerns_girl_mother\":\"\",\"e96c289e\":\"\",\"orther_org_reference_no\":\"\",\"_rev\":\"1-ec347c93b262e7db0e306b77f22c2e19\",\"evacuation_to\":\"\",\"disclosure_authorities\":\"\",\"c9fc0344\":\"\",\"wishes_telephone_2\":\"\",\"interview_date\":\"\",\"telephone\":\"\",\"evacuation_agent\":\"\",\"additional_tracing_info\":\"\",\"couchrest-type\":\"Child\",\"care_arrangements\":\"\",\"other_child_2_birthplace\":\"\",\"disclosure_public_relatives\":\"\",\"other_child_2_address\":\"\",\"wishes_name_2\":\"\",\"current_photo_key\":\"\",\"disclosure_public_name\":\"\",\"separation_details\":\"\",\"interview_subject_details\":\"\",\"wishes_address_2\":\"\",\"concerns_abuse_situation\":\"\",\"063c3784\":\"\",\"concerns_street_child\":\"\",\"other_child_3\":\"\",\"interview_subject\":\"\",\"care_arrangements_address\":\"\",\"documents\":\"\",\"other_child_1_birthplace\":\"\",\"fef83a5e\":\"\",\"is_father_alive\":\"\",\"created_by_full_name\":\"RapidFTR\",\"characteristics\":\"\",\"care_arrangements_familyinfo\":\"\",\"disclosure_deny_details\":\"\",\"other_org_name\":\"\",\"nationality\":\"\",\"short_id\":\"f52d5d1\",\"concerns_chh\":\"\",\"concerns_vulnerable_person\":\"\",\"wishes_telephone_3\":\"\",\"concerns_disabled\":\"\",\"fathers_name\":\"\",\"_id\":\"0369c92c8e2245e680dc9a580202e285\",\"other_org_country\":\"\",\"ethnicity_or_tribe\":\"\",\"care_arrangements_other\":\"\",\"wishes_name_3\":\"\"}";
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        Child child = new Child("id","user","{ 'name' : 'child1'}");
        Child syncedChild = new ChildService(context, repository, mockFluentRequest).sync(child, currentUser);
        assertThat(syncedChild.isSynced(), is(true));
        assertThat(syncedChild.getString("last_synced_at"), not(is(nullValue())));
        assertThat(syncedChild.getString("_attachments"), is(nullValue()));
    }

    @Test
    @Ignore
    public void shouldSetMediaIfNotAlreadyExistingOnTheMobile() throws JSONException, IOException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        RapidFtrApplication context = mockContext();
        String response = "{\"recorded_audio\":\"audio-12321\",\"photo_keys\": \"[photo-998,photo-888, photo-777]\",\"_id\":\"abcd\",\"current_photo_key\": \"photo-888\",\"separation_place\":\"\",\"wishes_address_3\":\"\",\"care_arrangments_name\":\"\",\"other_family\":\"\",\"care_arrangements_knowsfamily\":\"\",\"created_at\":\"2012-12-14 10:57:39UTC\",\"wishes_contacted_details\":\"\",\"posted_from\":\"Browser\"}";
        ChildService childService = spy(new ChildService(context, repository, mockFluentRequest));
        getFakeHttpLayer().setDefaultHttpResponse(200, response);
        Child child = new Child("id","user","{ 'name' : 'child1'}");

        doNothing().when(childService).getPhotoFromServer(Matchers.any(Child.class), Matchers.any(PhotoCaptureHelper.class), eq("photo-998"));
        doNothing().when(childService).getPhotoFromServer(Matchers.any(Child.class), Matchers.any(PhotoCaptureHelper.class), eq("photo-888"));
        doNothing().when(childService).getPhotoFromServer(Matchers.any(Child.class), Matchers.any(PhotoCaptureHelper.class),eq("photo-777"));

        childService.sync(child, currentUser);

        verify(mockFluentRequest).path("/api/children/abcd/audio");
        verify(childService).getPhotoFromServer(Matchers.any(Child.class), Matchers.any(PhotoCaptureHelper.class),eq("photo-888"));
        verify(childService).getPhotoFromServer(Matchers.any(Child.class), Matchers.any(PhotoCaptureHelper.class),eq("photo-998"));
        verify(childService).getPhotoFromServer(Matchers.any(Child.class), Matchers.any(PhotoCaptureHelper.class),eq("photo-777"));
    }

    @Test
    public void shouldFetchPrimaryPhotoFromServer() throws JSONException, IOException, GeneralSecurityException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        Child child = new Child("id1", "user1", "{ '_id' : '1234abcd' ,'current_photo_key' : 'image_file_name'}");
        getFakeHttpLayer().setDefaultHttpResponse(200, "image stream");

        new ChildService(mockContext(), repository, mockFluentRequest).getPhoto(child, "image_file_name");

        verify(mockFluentRequest).path("/api/children/1234abcd/photo/image_file_name");
    }

    @Test
    public void shouldFetchAudioFromServer() throws JSONException, IOException, GeneralSecurityException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        Child child = new Child("id1", "user1", "{ '_id' : '1234abcd' ,'recorded_audio' : 'audio_file_name'}");
        getFakeHttpLayer().setDefaultHttpResponse(200, "audio stream");

        new ChildService(mockContext(), repository, mockFluentRequest).getAudio(child);

        verify(mockFluentRequest).path("/api/children/1234abcd/audio");
    }

    @Test
    public void shouldFetchAllIdRevs() throws IOException, HttpException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());

        String response = "[{\"_rev\":\"5-1ed26a0e5072830a9064361a570684f6\",\"_id\":\"dfb2031ebfcbef39dccdb468f5200edc\"},{\"_rev\":\"4-b011946150a16b0d2c6271aed05e2abe\",\"_id\":\"59cd40f39ab6aa791f73885e3bdd99f9\"}]";
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        Map<String,String> allIdRevs = new ChildService(mockContext(), repository, mockFluentRequest).getAllIdsAndRevs();
        assertEquals(2, allIdRevs.size());
        assertEquals("5-1ed26a0e5072830a9064361a570684f6", allIdRevs.get("dfb2031ebfcbef39dccdb468f5200edc"));
        assertEquals("4-b011946150a16b0d2c6271aed05e2abe", allIdRevs.get("59cd40f39ab6aa791f73885e3bdd99f9"));

        verify(mockFluentRequest).path("/api/children/ids");
    }

    @Test
    public void shouldGetChild() throws IOException, JSONException {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        String response = "{\"separation_place\":\"\",\"wishes_address_3\":\"\",\"care_arrangments_name\":\"\",\"other_family\":\"\",\"care_arrangements_knowsfamily\":\"\",\"created_at\":\"2012-12-14 10:57:39UTC\",\"wishes_contacted_details\":\"\",\"posted_from\":\"Browser\",\"care_arrangements_relationship\":\"\",\"interviewer\":\"\",\"birthplace\":\"\",\"father_death_details\":\"\",\"mothers_name\":\"\",\"name\":\"kavitha working\",\"other_child_2_relationship\":\"\",\"other_child_1\":\"\",\"other_child_1_dob\":\"\",\"other_child_2_telephone\":\"\",\"caregivers_name\":\"\",\"other_child_3_dob\":\"\",\"concerns_medical_case\":\"\",\"names_origin\":\"\",\"gender\":\"\",\"unique_identifier\":\"8a126c33-d2e3-4802-8698-19c06f52d5d1\",\"is_caregiver_alive\":\"\",\"wishes_contacted\":\"\",\"other_child_3_address\":\"\",\"evacuation_from\":\"\",\"photo_keys\":[],\"address\":\"\",\"disclosure_other_orgs\":\"\",\"concerns_other\":\"\",\"histories\":[],\"wishes_wants_contact\":\"\",\"wishes_telephone_1\":\"\",\"posted_at\":\"2012-12-14 10:57:39UTC\",\"other_child_1_address\":\"\",\"other_child_3_birthplace\":\"\",\"other_child_3_relationship\":\"\",\"languages\":\"\",\"concerns_followup_details\":\"\",\"other_org_interview_status\":\"\",\"concerns_further_info\":\"\",\"concerns_needs_followup\":\"\",\"disclosure_public_photo\":\"\",\"wishes_name_1\":\"\",\"created_by\":\"rapidftr\",\"other_org_date\":\"\",\"wishes_address_1\":\"\",\"is_mother_alive\":\"\",\"other_child_1_relationship\":\"\",\"other_child_1_telephone\":\"\",\"interview_place\":\"\",\"evacuation_date\":\"\",\"evacuation_status\":\"\",\"other_child_2\":\"\",\"c206ec4e\":\"\",\"other_child_2_dob\":\"\",\"interviewers_org\":\"\",\"dob_or_age\":\"\",\"id_document\":\"\",\"care_arrangements_arrival_date\":\"\",\"rc_id_no\":\"\",\"care_arrangements_came_from\":\"\",\"protection_status\":\"\",\"other_org_place\":\"\",\"separation_date\":\"\",\"created_organisation\":\"N/A\",\"mother_death_details\":\"\",\"concerns_girl_mother\":\"\",\"e96c289e\":\"\",\"orther_org_reference_no\":\"\",\"_rev\":\"1-ec347c93b262e7db0e306b77f22c2e19\",\"evacuation_to\":\"\",\"disclosure_authorities\":\"\",\"c9fc0344\":\"\",\"wishes_telephone_2\":\"\",\"interview_date\":\"\",\"telephone\":\"\",\"evacuation_agent\":\"\",\"additional_tracing_info\":\"\",\"couchrest-type\":\"Child\",\"care_arrangements\":\"\",\"other_child_2_birthplace\":\"\",\"disclosure_public_relatives\":\"\",\"other_child_2_address\":\"\",\"wishes_name_2\":\"\",\"current_photo_key\":\"\",\"disclosure_public_name\":\"\",\"separation_details\":\"\",\"interview_subject_details\":\"\",\"wishes_address_2\":\"\",\"concerns_abuse_situation\":\"\",\"063c3784\":\"\",\"concerns_street_child\":\"\",\"other_child_3\":\"\",\"interview_subject\":\"\",\"care_arrangements_address\":\"\",\"documents\":\"\",\"other_child_1_birthplace\":\"\",\"fef83a5e\":\"\",\"is_father_alive\":\"\",\"created_by_full_name\":\"RapidFTR\",\"characteristics\":\"\",\"care_arrangements_familyinfo\":\"\",\"disclosure_deny_details\":\"\",\"other_org_name\":\"\",\"nationality\":\"\",\"short_id\":\"f52d5d1\",\"concerns_chh\":\"\",\"concerns_vulnerable_person\":\"\",\"wishes_telephone_3\":\"\",\"concerns_disabled\":\"\",\"fathers_name\":\"\",\"_id\":\"0369c92c8e2245e680dc9a580202e285\",\"other_org_country\":\"\",\"ethnicity_or_tribe\":\"\",\"care_arrangements_other\":\"\",\"wishes_name_3\":\"\"}";
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        Child child = new ChildService(mockContext(), repository, mockFluentRequest).getChild("0369c92c8e2245e680dc9a580202e285");
        assertEquals("kavitha working", child.get("name"));
        assertEquals("1-ec347c93b262e7db0e306b77f22c2e19", child.get("_rev"));

        verify(mockFluentRequest).path("/api/children/0369c92c8e2245e680dc9a580202e285");
    }

    @Test
    public void shouldMarkUnverifiedChildAsSyncedOnceSuccessfullySynced() throws Exception {
        FluentRequest mockFluentRequest = spy(new FluentRequest());
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children/unverified", new TestHttpResponse(200, "{}"));
        Child child = new Child();
        given(currentUser.isVerified()).willReturn(false);

        child = new ChildService(mockContext(), repository, mockFluentRequest).sync(child, currentUser);

        assertThat(child.isSynced(), is(true));
    }

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }

}
