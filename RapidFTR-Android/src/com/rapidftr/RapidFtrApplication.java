package com.rapidftr;

import com.rapidftr.forms.ChildDetailsForm;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.droidfu.DroidFuApplication;

public class RapidFtrApplication extends DroidFuApplication {

    private static String formSectionsTemplate;

    private static boolean loggedIn;

    public static String getFormSectionsBody() {
        return formSectionsTemplate;
    }

    public static void setFormSectionsTemplate(String formSectionsTemplate) {
        RapidFtrApplication.formSectionsTemplate = formSectionsTemplate;
    }

    public static List<ChildDetailsForm> getChildFormSections() throws Exception{
        List<ChildDetailsForm> formList = Arrays.asList(new ObjectMapper().readValue(get_sample_data(), ChildDetailsForm[].class));
        Collections.sort(formList);
        return formList;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        RapidFtrApplication.loggedIn = loggedIn;
    }

    // TODO - this is sample data cleanit up after dev complete.
    private static String get_sample_data(){

     return "[" +
            "   {" +
            "      \"name\":\"Basic Identity\"," +
            "      \"_rev\":\"3-033b201a9f1efc17ba67a28a91d95a48\"," +
            "      \"unique_id\":\"basic_identity\"," +
            "      \"_id\":\"dba78f506ab9e6e99a2ce53c4d19b574\"," +
            "      \"order\":1," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"order\":1," +
            "               \"highlighted\":true" +
            "            }," +
            "            \"editable\":false," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Name\"" +
            "         }," +
            "         {" +
            "            \"name\":\"rc_id_no\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"order\":2," +
            "               \"highlighted\":true" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"RC ID No.\"" +
            "         }," +
            "         {" +
            "            \"name\":\"protection_status\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Unaccompanied\"," +
            "               \"Separated\"" +
            "            ]," +
            "            \"help_text\":\"A separated child is any person under the age of 18, separated from both parents or from his/her previous legal or customary primary care giver, but not necessarily from other relatives. An unaccompanied child is any person who meets those criteria but is ALSO separated from his/her relatives.\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"order\":3," +
            "               \"highlighted\":true" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Protection Status\"" +
            "         }," +
            "         {" +
            "            \"name\":\"id_document\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Personal ID Document No.\"" +
            "         }," +
            "         {" +
            "            \"name\":\"gender\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Male\"," +
            "               \"Female\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Sex\"" +
            "         }," +
            "         {" +
            "            \"name\":\"nick_name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Also Known As (nickname)\"" +
            "         }," +
            "         {" +
            "            \"name\":\"names_origin\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Name(s) given to child after separation?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"dob_or_age\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date of Birth / Age\"" +
            "         }," +
            "         {" +
            "            \"name\":\"birthplace\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Birthplace\"" +
            "         }," +
            "         {" +
            "            \"name\":\"nationality\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Nationality\"" +
            "         }," +
            "         {" +
            "            \"name\":\"ethnicity_or_tribe\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Ethnic group / tribe\"" +
            "         }," +
            "         {" +
            "            \"name\":\"languages\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"order\":4," +
            "               \"highlighted\":true" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Languages spoken\"" +
            "         }," +
            "         {" +
            "            \"name\":\"characteristics\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Distinguishing Physical Characteristics\"" +
            "         }," +
            "         {" +
            "            \"name\":\"documents\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Documents carried by the child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"current_photo_key\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"photo_upload_box\"," +
            "            \"display_name\":\"Current Photo Key\"" +
            "         }," +
            "         {" +
            "            \"name\":\"recorded_audio\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"audio_upload_box\"," +
            "            \"display_name\":\"Recorded Audio\"" +
            "         }," +
            "         {" +
            "            \"name\":\"asdffffffffffffffffffsafsadfsdafsdafdsfsdfsdklsajflsjaflksdajflksdajflksajlfkdsalfksadlfjsdalfjsdalkfjsdlakfjsdlkafjldsakfjlsadjflsadkj\"," +
            "            \"help_text\":\"asfasdfsadfsdf\"," +
            "            \"enabled\":\"true\"," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"type\":\"text_field\"," +
            "            \"editable\":true," +
            "            \"display_name\":\"asdffffffffffffffffffsafsadfsdafsdafdsfsdfsdklsajflsjaflksdajflksdajflksajlfkdsalfksad;lfjsdal;fjsdal;kfjsdla;kfjsdlkafjldsakfjlsadjfl;sadkj\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"Basic identity information about a separated or unaccompanied child.\"," +
            "      \"perm_enabled\":true" +
            "   }," +
            "   {" +
            "      \"name\":\"Family details\"," +
            "      \"_rev\":\"1-19ee427ecda43dbe4d70bf64d716f1aa\"," +
            "      \"unique_id\":\"family_details\"," +
            "      \"_id\":\"a39132248306dc446e6b7c8034804cb4\"," +
            "      \"order\":2," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"fathers_name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Father's Name\"" +
            "         }," +
            "         {" +
            "            \"name\":\"is_father_alive\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Unknown\"," +
            "               \"Alive\"," +
            "               \"Dead\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Is Father Alive?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"father_death_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"If dead, please provide details\"" +
            "         }," +
            "         {" +
            "            \"name\":\"mothers_name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Mother's Name\"" +
            "         }," +
            "         {" +
            "            \"name\":\"is_mother_alive\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Unknown\"," +
            "               \"Alive\"," +
            "               \"Dead\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Is Mother Alive?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"mother_death_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"If dead, please provide details\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_family\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Other persons well known to the child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"address\"," +
            "            \"help_text\":\"If the child does not remember his/her address, please note other relevant information, such as descriptions of mosques, churches, schools and other landmarks.\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Address of child before separation (and person with whom he/she lived)\"" +
            "         }," +
            "         {" +
            "            \"name\":\"telephone\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone Before Separation\"" +
            "         }," +
            "         {" +
            "            \"name\":\"caregivers_name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Caregiver's Name (if different)\"" +
            "         }," +
            "         {" +
            "            \"name\":\"is_caregiver_alive\"," +
            "            \"option_strings\":[" +
            "               \"Unknown\"," +
            "               \"Alive\"," +
            "               \"Dead\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Is Caregiver Alive?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_1\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"1) Sibling or other child accompanying the child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_1_relationship\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Relationship\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_1_dob\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date of Birth\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_1_birthplace\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Birthplace\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_1_address\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Current Address\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_1_telephone\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_2\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"2) Sibling or other child accompanying the child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_2_relationship\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Relationship\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_2_dob\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date of Birth\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_2_birthplace\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Birthplace\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_2_address\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Current Address\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_2_telephone\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_3\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"3) Sibling or other child accompanying the child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_3_relationship\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Relationship\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_3_dob\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date of Birth\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_3_birthplace\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Birthplace\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_3_address\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Current Address\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_child_3_telephone\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"Information about a child's known family\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Care Arrangements\"," +
            "      \"_rev\":\"1-8597f593bf8f3309995927c349ec75cc\"," +
            "      \"unique_id\":\"care_arrangements\"," +
            "      \"_id\":\"96ed2b87460e8534372ed5cacb79fdd7\"," +
            "      \"order\":3," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"care_arrangements\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Children's Center\"," +
            "               \"Other Family Member(s)\"," +
            "               \"Foster Family\"," +
            "               \"Alone\"," +
            "               \"Other\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Current Care Arrangements\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_other\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"If other, please provide details.\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangments_name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Full Name\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_relationship\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Relationship To Child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_knowsfamily\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Does the caregiver know the family of the child?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_familyinfo\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Caregiver's information about child or family\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_address\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Child's current address (of caretaker or centre)\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_came_from\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Child Arriving From\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_arrival_date\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Arrival Date\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"Information about the child's current caregiver\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Separation History\"," +
            "      \"_rev\":\"1-8d7b93eb80a0d314d2eef3241be9e9ea\"," +
            "      \"unique_id\":\"separation_history\"," +
            "      \"_id\":\"edc515c4b941a57fbd3bf638d6f32d91\"," +
            "      \"order\":4," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"separation_date\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date of Separation\"" +
            "         }," +
            "         {" +
            "            \"name\":\"separation_place\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Place of Separation.\"" +
            "         }," +
            "         {" +
            "            \"name\":\"separation_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Circumstances of Separation (please provide details)\"" +
            "         }," +
            "         {" +
            "            \"name\":\"evacuation_status\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Has child been evacuated?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"evacuation_agent\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"If yes, through which organization?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"evacuation_from\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Evacuated From\"" +
            "         }," +
            "         {" +
            "            \"name\":\"evacuation_to\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Evacuated To\"" +
            "         }," +
            "         {" +
            "            \"name\":\"evacuation_date\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Evacuation Date\"" +
            "         }," +
            "         {" +
            "            \"name\":\"care_arrangements_arrival_date\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Arrival Date\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"The child's separation and evacuation history.\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Protection Concerns\"," +
            "      \"_rev\":\"1-3e63e80d1ef7c9a1000dc1ad31685b07\"," +
            "      \"unique_id\":\"protection_concerns\"," +
            "      \"_id\":\"998a7c38ea4a4a35943007e6b50a547d\"," +
            "      \"order\":5," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"concerns_chh\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Child Headed Household\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_disabled\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Disabled Child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_medical_case\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Medical Case\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_street_child\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Street Child\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_girl_mother\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Girl Mother\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_vulnerable_person\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Living with Vulnerable Person\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_abuse_situation\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Girl Mother\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_other\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Other (please specify)\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_further_info\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Further Information\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_needs_followup\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Specific Follow-up Required?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"concerns_followup_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Please specify follow-up needs.\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Childs Wishes\"," +
            "      \"_rev\":\"1-b8f8e36d718076ee7e7c940990171037\"," +
            "      \"unique_id\":\"childs_wishes\"," +
            "      \"_id\":\"f006aa8728bf467692cac8ae8f701e4b\"," +
            "      \"order\":6," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"wishes_name_1\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Person child wishes to locate - Preferred\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_telephone_1\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_address_1\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Last Known Address\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_name_2\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Person child wishes to locate - Second Choice\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_telephone_2\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_address_2\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Last Known Address\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_name_3\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Person child wishes to locate - Third Choice\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_telephone_3\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Telephone\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_address_3\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Last Known Address\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_contacted\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Has the child heard from / been in contact with any relatives?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_contacted_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Please give details\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_wants_contact\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes, as soon as possible\"," +
            "               \"Yes, later\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Does child want to be reunited with family?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"wishes_contacted_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Please explain why\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Other Interviews\"," +
            "      \"_rev\":\"1-99e9d9faa3798a21fa38c5c84f1a8c14\"," +
            "      \"unique_id\":\"other_interviews\"," +
            "      \"_id\":\"c02b3b71f411e9fada2ab75ed8168bed\"," +
            "      \"order\":7," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"other_org_interview_status\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Has the child been interviewed by another organization?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_org_name\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Name of Organization\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_org_place\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Place of Interview\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_org_country\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Country\"" +
            "         }," +
            "         {" +
            "            \"name\":\"other_org_date\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date\"" +
            "         }," +
            "         {" +
            "            \"name\":\"orther_org_reference_no\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Reference No. given to child by other organization\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Other Tracing Info\"," +
            "      \"_rev\":\"1-c8c340d12d5fc403fb3fe1414960a280\"," +
            "      \"unique_id\":\"other_tracing_info\"," +
            "      \"_id\":\"4181d9d53609432e0467a6eb1d31f40f\"," +
            "      \"order\":8," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"additional_tracing_info\"," +
            "            \"help_text\":\"Such as key persons/location in the life of the child who/which might provide information about the location of the sought family -- e.g. names of religious leader, market place, etc. Please ask the child where he/she thinks relatives and siblings might be, and if the child is in contact with any family friends. Include any useful information the caregiver provides.\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"Additional Info That Could Help In Tracing?\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"\"," +
            "      \"perm_enabled\":false" +
            "   }," +
            "   {" +
            "      \"name\":\"Interview Details\"," +
            "      \"_rev\":\"1-3f19fbab73aa90c07bc678e3dd17266c\"," +
            "      \"unique_id\":\"interview_details\"," +
            "      \"_id\":\"2c60a9abe7feedc5c7445000e8811adc\"," +
            "      \"order\":9," +
            "      \"enabled\":true," +
            "      \"fields\":[" +
            "         {" +
            "            \"name\":\"disclosure_public_name\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Does Child/Caregiver agree to share name on posters/radio/Internet?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"disclosure_public_photo\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Photo?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"disclosure_public_relatives\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Names of Relatives?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"disclosure_other_orgs\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Does Child/Caregiver agree to share collected information with other organizations?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"disclosure_authorities\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"Yes\"," +
            "               \"No\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"The authorities?\"" +
            "         }," +
            "         {" +
            "            \"name\":\"disclosure_deny_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"textarea\"," +
            "            \"display_name\":\"If child does not agree, specify what cannot be shared and why.\"" +
            "         }," +
            "         {" +
            "            \"name\":\"interview_place\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Place of Interview\"" +
            "         }," +
            "         {" +
            "            \"name\":\"interview_date\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Date\"" +
            "         }," +
            "         {" +
            "            \"name\":\"interview_subject\"," +
            "            \"option_strings\":[" +
            "               \"\"," +
            "               \"the child\"," +
            "               \"caregiver\"," +
            "               \"other\"" +
            "            ]," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"select_box\"," +
            "            \"display_name\":\"Information Obtained From\"" +
            "         }," +
            "         {" +
            "            \"name\":\"interview_subject_details\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"If other, please specify\"" +
            "         }," +
            "         {" +
            "            \"name\":\"interviewer\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Name of Interviewer\"" +
            "         }," +
            "         {" +
            "            \"name\":\"interviewers_org\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Interviewer's Organization\"" +
            "         }," +
            "         {" +
            "            \"name\":\"governing_org\"," +
            "            \"enabled\":true," +
            "            \"highlight_information\":{" +
            "               \"highlighted\":false" +
            "            }," +
            "            \"editable\":true," +
            "            \"type\":\"text_field\"," +
            "            \"display_name\":\"Organization in charge of tracing child's family\"" +
            "         }" +
            "      ]," +
            "      \"editable\":true," +
            "      \"couchrest-type\":\"FormSection\"," +
            "      \"description\":\"\"," +
            "      \"perm_enabled\":false" +
            "   }" +
            "]";
    }
}
