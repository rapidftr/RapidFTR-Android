package com.rapidftr.service;

import android.content.SharedPreferences;
import android.content.res.Resources;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.xtremelabs.robolectric.Robolectric.getFakeHttpLayer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(CustomTestRunner.class)
public class ChildServiceTest {

    @Test
    public void shouldParseJsonResponseAndConvertToChildren() throws IOException, JSONException {
        String response= "[{\"photo_keys\":[],\"evacuation_status\":\"\",\"id_document\":\"\",\"disclosure_authorities\":\"\",\"wishes_address_1\":\"\",\"ethnicity_or_tribe\":\"\",\"evacuation_date\":\"\",\"other_child_1_telephone\":\"\",\"concerns_followup_details\":\"\",\"nick_name\":\"\",\"rc_id_no\":\"\",\"wishes_name_3\":\"\",\"disclosure_deny_details\":\"\",\"concerns_needs_followup\":\"\",\"interview_place\":\"\",\"care_arrangements\":\"\",\"concerns_further_info\":\"\",\"fathers_name\":\"\",\"wishes_wants_contact\":\"\",\"governing_org\":\"\",\"disclosure_public_name\":\"\",\"other_child_2_telephone\":\"\",\"separation_details\":\"\",\"care_arrangements_address\":\"\",\"other_child_3\":\"\",\"caregivers_name\":\"\",\"separation_place\":\"\",\"mothers_name\":\"\",\"concerns_other\":\"\",\"wishes_telephone_2\":\"\",\"is_caregiver_alive\":\"\",\"disclosure_public_photo\":\"\",\"care_arrangements_came_from\":\"\",\"other_child_3_dob\":\"\",\"wishes_address_2\":\"\",\"couchrest-type\":\"Child\",\"_id\":\"be5d92ff528c8d82c8753934a7712eb1\",\"additional_tracing_info\":\"\",\"care_arrangments_name\":\"\",\"concerns_girl_mother\":\"\",\"_rev\":\"1-dfd36730cc723481387511804b21a320\",\"mother_death_details\":\"\",\"interview_subject\":\"\",\"other_org_place\":\"\",\"other_org_name\":\"\",\"care_arrangements_relationship\":\"\",\"wishes_name_1\":\"\",\"concerns_medical_case\":\"\",\"other_child_3_telephone\":\"\",\"disclosure_public_relatives\":\"\",\"concerns_chh\":\"\",\"wishes_contacted\":\"\",\"name\":\"Akash Bhalla\",\"unique_identifier\":\"fworkerxxxdea2f\",\"evacuation_from\":\"\",\"interview_subject_details\":\"\",\"father_death_details\":\"\",\"interview_date\":\"\",\"characteristics\":\"\",\"documents\":\"\",\"other_child_2_relationship\":\"\",\"evacuation_to\":\"\",\"created_by_full_name\":\"testing\",\"is_father_alive\":\"\",\"other_child_1_address\":\"\",\"names_origin\":\"\",\"wishes_telephone_3\":\"\",\"other_child_1\":\"\",\"birthplace\":\"\",\"care_arrangements_arrival_date\":\"\",\"other_child_1_relationship\":\"\",\"other_child_3_birthplace\":\"\",\"concerns_street_child\":\"\",\"wishes_address_3\":\"\",\"telephone\":\"\",\"other_child_2_birthplace\":\"\",\"other_child_1_birthplace\":\"\",\"current_photo_key\":null,\"protection_status\":\"\",\"care_arrangements_knowsfamily\":\"\",\"care_arrangements_other\":\"\",\"histories\":[],\"interviewers_org\":\"\",\"created_by\":\"fworker\",\"other_child_3_relationship\":\"\",\"concerns_abuse_situation\":\"\",\"address\":\"\",\"wishes_name_2\":\"\",\"other_child_1_dob\":\"\",\"other_org_interview_status\":\"\",\"gender\":\"\",\"posted_from\":\"Browser\",\"nationality\":\"\",\"separation_date\":\"\",\"other_child_2_dob\":\"\",\"evacuation_agent\":\"\",\"wishes_contacted_details\":\"\",\"other_family\":\"\",\"created_at\":\"2012-10-26 09:28:39UTC\",\"languages\":\"\",\"other_child_3_address\":\"\",\"is_mother_alive\":\"\",\"other_org_country\":\"\",\"dob_or_age\":\"\",\"concerns_disabled\":\"\",\"other_org_date\":\"\",\"concerns_vulnerable_person\":\"\",\"care_arrangements_familyinfo\":\"\",\"disclosure_other_orgs\":\"\",\"interviewer\":\"\",\"orther_org_reference_no\":\"\",\"other_child_2\":\"\",\"wishes_telephone_1\":\"\",\"other_child_2_address\":\"\",\"posted_at\":\"2012-10-26 09:28:39UTC\"}]";

        getFakeHttpLayer().setDefaultHttpResponse(201, response);

        RapidFtrApplication context = mock(RapidFtrApplication.class, RETURNS_DEEP_STUBS);
        Resources resources = mock(Resources.class);
        given(context.getApplicationContext()).willReturn(context);
        SharedPreferences preferences = mock(SharedPreferences.class);
        given(preferences.getString("SERVER_URL", "")).willReturn("whatever");
        given(context.getSharedPreferences(RapidFtrApplication.SHARED_PREFERENCES_FILE, 0)).willReturn(preferences);
        given(context.getResources()).willReturn(resources);

        List<Child> children = new ChildService(context).getAllChildren();

        assertThat(children.size(), is(1));
        assertThat(children.get(0).getId(), is("be5d92ff528c8d82c8753934a7712eb1"));
    }

}
