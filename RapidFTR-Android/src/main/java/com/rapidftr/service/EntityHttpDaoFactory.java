package com.rapidftr.service;

import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.PotentialMatch;

public class EntityHttpDaoFactory {

    public static EntityHttpDao<Enquiry> createEnquiryHttpDao(String serverUrl, String apiPath, String apiParameter) {
        return new EntityHttpDao<Enquiry>(serverUrl, apiPath, apiParameter) {
        };
    }

    public static EntityHttpDao<PotentialMatch> createPotentialMatchHttpDao(String serverUrl, String apiPath, String apiParameter) {
        return new EntityHttpDao<PotentialMatch>(serverUrl, apiPath, apiParameter) {
        };
    }

    public static EntityHttpDao<Child> createChildHttpDao(String serverUrl, String apiPath, String apiParameter) {
        return new EntityHttpDao<Child>(serverUrl, apiPath, apiParameter) {
        };
    }
}
