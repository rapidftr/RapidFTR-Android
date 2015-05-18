package com.rapidftr.service;

import android.graphics.BitmapFactory;
import com.google.common.io.CharStreams;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.database.Database;
import com.rapidftr.model.Child;
import com.rapidftr.model.History;
import com.rapidftr.model.User;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.AudioCaptureHelper;
import com.rapidftr.utils.PhotoCaptureHelper;
import com.rapidftr.utils.http.FluentRequest;
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
import org.robolectric.Robolectric;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.TestHttpResponse;

import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;
import static com.rapidftr.RapidFtrApplication.getApplicationInstance;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.getFakeHttpLayer;

@RunWith(CustomTestRunner.class)
public class ChildSyncServiceTest {

    @Mock
    private ChildRepository repository;
    @Mock
    private User currentUser;

    private RapidFtrApplication application;

    FluentRequest fluentRequest;
    public static final String RESPONSE = "{\"unique_identifier\":\"adf7c0c9-0137-4cae-beea-b7d282344829\",\"created_at\":\"2013-02-08 12:18:37\",\"created_by_full_name\":\"RapidFTR\",\"couchrest-type\":\"Child\",\"short_id\":\"2344829\",\"_id\":\"b7f89b978870da823e0af6491c3e295b\",\"_rev\":\"2-bc72af384e177fcaa8e9e8d181bfe05b\",\"name\":\"\",\"last_updated_at\":\"2013-02-08 11:37:33\",\"current_photo_key\":\"photo--1475374810-2013-02-08T175138\",\"created_by\":\"rapidftr\",\"photo_keys\":[\"photo--1475374810-2013-02-08T175138\"],\"created_organisation\":\"N/A\",\"posted_at\":\"2013-02-08 12:16:55UTC\",\"last_updated_by_full_name\":\"RapidFTR\"}";
    private EntityHttpDao<Child> childHttpDao;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        application = (RapidFtrApplication) Robolectric.getShadowApplication().getApplicationContext();
        User user = new User("userName", "password", true, "http://1.2.3.4");
        application.setCurrentUser(user);

        childHttpDao = EntityHttpDaoFactory.createChildHttpDao(application,
                "http://whatever",
                ChildSyncService.CHILDREN_API_PATH,
                ChildSyncService.CHILDREN_API_PARAMETER);
        given(currentUser.isVerified()).willReturn(true);
        fluentRequest = new FluentRequest();
    }

    @Test
    public void shouldFetchListOfResourceUrlsToUpdate() throws Exception {
        String response = "[{\"location\":\"http://whatever/api/children/5-1ed26a0e5072830a9064361a570684f6\"},{\"location\":\"http://whatever/api/children/4-b011946150a16b0d2c6271aed05e2abe\"}]";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children?updated_after=1970-01-01%2000%3A00%3A00UTC", response);

        List<String> idsToChange = new ChildSyncService(mockContext(), childHttpDao, repository).getIdsToDownload();
        assertEquals(2, idsToChange.size());
        assertEquals("http://whatever/api/children/5-1ed26a0e5072830a9064361a570684f6", idsToChange.get(0));
    }

    @Test
    public void shouldUseLastChildSyncTimestampToRetreiveIds() throws Exception {
        String response = "[{\"location\":\"http://whatever/api/children/5-1ed26a0e5072830a9064361a570684f6\"},{\"location\":\"http://whatever/api/children/4-b011946150a16b0d2c6271aed05e2abe\"}]";
        long time = 1412330399491l;
        getApplicationInstance().getSharedPreferences().edit().putLong(RapidFtrApplication.LAST_CHILD_SYNC, time).commit();

        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children?updated_after=2014-10-03%2009%3A59%3A59UTC", response);
        new ChildSyncService(mockContext(), childHttpDao, repository).getIdsToDownload();
    }

    @Test
    public void shouldGetChild() throws IOException, JSONException, HttpException {
        String response = "{\"separation_place\":\"\",\"wishes_address_3\":\"\",\"care_arrangments_name\":\"\",\"other_family\":\"\",\"care_arrangements_knowsfamily\":\"\",\"created_at\":\"2012-12-14 10:57:39UTC\",\"wishes_contacted_details\":\"\",\"posted_from\":\"Browser\",\"care_arrangements_relationship\":\"\",\"interviewer\":\"\",\"birthplace\":\"\",\"father_death_details\":\"\",\"mothers_name\":\"\",\"name\":\"kavitha working\",\"other_child_2_relationship\":\"\",\"other_child_1\":\"\",\"other_child_1_dob\":\"\",\"other_child_2_telephone\":\"\",\"caregivers_name\":\"\",\"other_child_3_dob\":\"\",\"concerns_medical_case\":\"\",\"names_origin\":\"\",\"gender\":\"\",\"unique_identifier\":\"8a126c33-d2e3-4802-8698-19c06f52d5d1\",\"is_caregiver_alive\":\"\",\"wishes_contacted\":\"\",\"other_child_3_address\":\"\",\"evacuation_from\":\"\",\"photo_keys\":[],\"address\":\"\",\"disclosure_other_orgs\":\"\",\"concerns_other\":\"\",\"histories\":[],\"wishes_wants_contact\":\"\",\"wishes_telephone_1\":\"\",\"posted_at\":\"2012-12-14 10:57:39UTC\",\"other_child_1_address\":\"\",\"other_child_3_birthplace\":\"\",\"other_child_3_relationship\":\"\",\"languages\":\"\",\"concerns_followup_details\":\"\",\"other_org_interview_status\":\"\",\"concerns_further_info\":\"\",\"concerns_needs_followup\":\"\",\"disclosure_public_photo\":\"\",\"wishes_name_1\":\"\",\"created_by\":\"rapidftr\",\"other_org_date\":\"\",\"wishes_address_1\":\"\",\"is_mother_alive\":\"\",\"other_child_1_relationship\":\"\",\"other_child_1_telephone\":\"\",\"interview_place\":\"\",\"evacuation_date\":\"\",\"evacuation_status\":\"\",\"other_child_2\":\"\",\"c206ec4e\":\"\",\"other_child_2_dob\":\"\",\"interviewers_org\":\"\",\"dob_or_age\":\"\",\"id_document\":\"\",\"care_arrangements_arrival_date\":\"\",\"rc_id_no\":\"\",\"care_arrangements_came_from\":\"\",\"protection_status\":\"\",\"other_org_place\":\"\",\"separation_date\":\"\",\"created_organisation\":\"N/A\",\"mother_death_details\":\"\",\"concerns_girl_mother\":\"\",\"e96c289e\":\"\",\"orther_org_reference_no\":\"\",\"_rev\":\"1-ec347c93b262e7db0e306b77f22c2e19\",\"evacuation_to\":\"\",\"disclosure_authorities\":\"\",\"c9fc0344\":\"\",\"wishes_telephone_2\":\"\",\"interview_date\":\"\",\"telephone\":\"\",\"evacuation_agent\":\"\",\"additional_tracing_info\":\"\",\"couchrest-type\":\"Child\",\"care_arrangements\":\"\",\"other_child_2_birthplace\":\"\",\"disclosure_public_relatives\":\"\",\"other_child_2_address\":\"\",\"wishes_name_2\":\"\",\"current_photo_key\":\"\",\"disclosure_public_name\":\"\",\"separation_details\":\"\",\"interview_subject_details\":\"\",\"wishes_address_2\":\"\",\"concerns_abuse_situation\":\"\",\"063c3784\":\"\",\"concerns_street_child\":\"\",\"other_child_3\":\"\",\"interview_subject\":\"\",\"care_arrangements_address\":\"\",\"documents\":\"\",\"other_child_1_birthplace\":\"\",\"fef83a5e\":\"\",\"is_father_alive\":\"\",\"created_by_full_name\":\"RapidFTR\",\"characteristics\":\"\",\"care_arrangements_familyinfo\":\"\",\"disclosure_deny_details\":\"\",\"other_org_name\":\"\",\"nationality\":\"\",\"short_id\":\"f52d5d1\",\"concerns_chh\":\"\",\"concerns_vulnerable_person\":\"\",\"wishes_telephone_3\":\"\",\"concerns_disabled\":\"\",\"fathers_name\":\"\",\"_id\":\"0369c92c8e2245e680dc9a580202e285\",\"other_org_country\":\"\",\"ethnicity_or_tribe\":\"\",\"care_arrangements_other\":\"\",\"wishes_name_3\":\"\"}";
        getFakeHttpLayer().setDefaultHttpResponse(200, response);

        String resourceUrl = "http://whatever/api/children/0369c92c8e2245e680dc9a580202e285";
        getFakeHttpLayer().addHttpResponseRule("http://whatever/api/children/0369c92c8e2245e680dc9a580202e285/", response);

        Child child = new ChildSyncService(mockContext(), childHttpDao, repository).getRecord(resourceUrl);
        assertEquals("kavitha working", child.get("name"));
        assertEquals("1-ec347c93b262e7db0e306b77f22c2e19", child.get("_rev"));
    }

    @Test
    public void shouldNotIncludeCouchDbIdInSyncPathIfUnavailable() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        ChildSyncService childSyncService = new ChildSyncService(mockContext(), childHttpDao, repository);
        assertEquals("/api/children", childSyncService.getSyncPath(child, currentUser));
    }

    @Test
    public void shouldIncludeCouchDbIdInSyncPathIfAvailable() throws Exception {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        child.put(Database.ChildTableColumn.internal_id.getColumnName(), "xyz");
        ChildSyncService childSyncService = new ChildSyncService(mockContext(), childHttpDao, repository);
        assertEquals("/api/children/xyz", childSyncService.getSyncPath(child, currentUser));
    }

    @Test
    public void shouldReutrnUnverifiedPathIfUserIsNotVerified() throws JSONException {
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        doReturn(false).when(currentUser).isVerified();
        ChildSyncService childSyncService = new ChildSyncService(mockContext(), childHttpDao, repository);
        assertEquals("/api/children/unverified", childSyncService.getSyncPath(child, currentUser));
    }

    private RapidFtrApplication mockContext() {
        RapidFtrApplication context = RapidFtrApplication.getApplicationInstance();
        context.getSharedPreferences().edit().putString(SERVER_URL_PREF, "whatever").commit();
        return context;
    }
}
